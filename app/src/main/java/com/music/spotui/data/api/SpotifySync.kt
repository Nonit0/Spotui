package com.music.spotui.data.api

import android.content.Context
import android.util.Log
import com.metrolist.spotify.Spotify
import com.metrolist.spotify.models.SpotifyPlaylist
import com.metrolist.spotify.models.SpotifyPlaylistTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Fire-and-forget mirroring of local library actions to the user's REAL Spotify
 * account (web-player token): liking a song, saving an album or following an
 * artist here shows up in Spotify itself. Failures are logged, never surfaced —
 * the local action already succeeded.
 */
object SpotifySync {

    private const val TAG = "SpotifySync"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun setTrackSaved(context: Context, trackId: String, saved: Boolean) =
        setSaved(context, trackId, "spotify:track:$trackId", saved)

    fun setAlbumSaved(context: Context, albumId: String, saved: Boolean) =
        setSaved(context, albumId, "spotify:album:$albumId", saved)

    fun setArtistFollowed(context: Context, artistId: String, followed: Boolean) =
        setSaved(context, artistId, "spotify:artist:$artistId", followed)

    private fun setSaved(context: Context, id: String, uri: String, saved: Boolean) {
        if (id.isBlank()) return
        val app = context.applicationContext
        scope.launch {
            if (!SpotifyTokenProvider.ensureToken(app)) {
                Log.w(TAG, "no token — skipped syncing $uri saved=$saved")
                return@launch
            }
            val result =
                if (saved) Spotify.addToLibrary(listOf(uri))
                else Spotify.removeFromLibrary(listOf(uri))
            result.fold(
                onSuccess = { Log.d(TAG, "synced $uri saved=$saved") },
                onFailure = { Log.w(TAG, "failed syncing $uri saved=$saved", it) },
            )
        }
    }

    /** Adds a track to one of the user's playlists on Spotify itself. */
    fun addTrackToPlaylist(context: Context, playlistId: String, trackId: String, onDone: (Boolean) -> Unit = {}) {
        if (playlistId.isBlank() || trackId.isBlank()) { onDone(false); return }
        val app = context.applicationContext
        scope.launch {
            val ok = SpotifyTokenProvider.ensureToken(app) &&
                Spotify.addTracksToPlaylist(playlistId, listOf("spotify:track:$trackId")).isSuccess
            if (ok) {
                membershipCache[playlistId]?.add(trackId)
                Api.HomeCache.invalidatePlaylist(playlistId)
            }
            if (!ok) Log.w(TAG, "failed adding track $trackId to playlist $playlistId")
            onDone(ok)
        }
    }

    /** Removes every occurrence of a track from one of the user's playlists on
     *  Spotify itself (resolves the playlist-scoped item uids first). */
    fun removeTrackFromPlaylist(context: Context, playlistId: String, trackId: String, onDone: (Boolean) -> Unit = {}) {
        if (playlistId.isBlank() || trackId.isBlank()) { onDone(false); return }
        val app = context.applicationContext
        scope.launch {
            if (!SpotifyTokenProvider.ensureToken(app)) { onDone(false); return@launch }
            val uri = "spotify:track:$trackId"
            val items = allPlaylistItems(playlistId)
            if (items == null) { Log.w(TAG, "couldn't read playlist $playlistId to remove $trackId"); onDone(false); return@launch }
            val refs = items
                .filter { it.track?.id == trackId || it.track?.uri == uri }
                .mapNotNull { pt -> pt.uid?.let { Spotify.PlaylistItemRef(uri = uri, uid = it) } }
            val ok = refs.isNotEmpty() &&
                Spotify.removeTracksFromPlaylist(playlistId, refs).isSuccess
            if (ok) {
                membershipCache[playlistId]?.remove(trackId)
                Api.HomeCache.invalidatePlaylist(playlistId)
            }
            if (!ok) Log.w(TAG, "failed removing track $trackId from playlist $playlistId")
            onDone(ok)
        }
    }

    /** Creates a new playlist on Spotify itself, adding [trackId] to it when given.
     *  Returns the playlist, or null when it could not be created (no session,
     *  network or Spotify error) so the caller can fall back to a local one. */
    suspend fun createPlaylist(context: Context, name: String, trackId: String = ""): SpotifyPlaylist? {
        if (name.isBlank()) return null
        if (!SpotifyTokenProvider.ensureToken(context.applicationContext)) return null
        val playlist = Spotify.createPlaylist(name).getOrNull()
        if (playlist == null || playlist.id.isBlank()) {
            Log.w(TAG, "createPlaylist '$name' failed"); return null
        }
        membershipCache[playlist.id] = ConcurrentHashMap.newKeySet<String>()
        if (trackId.isNotBlank()) {
            if (Spotify.addTracksToPlaylist(playlist.id, listOf("spotify:track:$trackId")).isSuccess) {
                membershipCache[playlist.id]?.add(trackId)
            } else {
                Log.w(TAG, "created playlist ${playlist.id} but failed adding track $trackId")
            }
        }
        Api.HomeCache.library = null
        return playlist
    }

    /** Every item of [playlistId], walking all pages of 100 (not just the first one),
     *  or null when a page can't be fetched (so callers don't mistake a partial
     *  read for "the track isn't there"). */
    private suspend fun allPlaylistItems(playlistId: String): List<SpotifyPlaylistTrack>? {
        val pageSize = 100
        val all = mutableListOf<SpotifyPlaylistTrack>()
        var offset = 0
        // Hard cap (Spotify playlists top out around 10k items) so a bad `total` can't loop forever.
        while (offset < 20_000) {
            val page = Spotify.playlistTracks(playlistId, limit = pageSize, offset = offset).getOrNull()
                ?: return null
            all += page.items
            offset += pageSize
            val done = if (page.total > 0) offset >= page.total else page.items.size < pageSize
            if (page.items.isEmpty() || done) break
        }
        return all
    }

    // ── Playlist membership (which playlists contain a track) ──
    // Cached per playlist for the app session so the "Saved in" sheet can show
    // check marks without re-fetching on every open.
    private val membershipCache = ConcurrentHashMap<String, MutableSet<String>>()

    /** Track ids contained in [playlistId] (first call fetches, then cached). */
    suspend fun playlistTrackIds(context: Context, playlistId: String): Set<String> {
        membershipCache[playlistId]?.let { return it }
        if (!SpotifyTokenProvider.ensureToken(context.applicationContext)) return emptySet()
        // A failed read is not cached, so the next open retries instead of showing
        // the song as "not in this playlist" for the rest of the session.
        val items = allPlaylistItems(playlistId) ?: return emptySet()
        val ids = items
            .mapNotNull { it.track?.id?.ifBlank { null } }
            .toMutableSet()
        val set = ConcurrentHashMap.newKeySet<String>().apply { addAll(ids) }
        membershipCache[playlistId] = set
        return set
    }
}
