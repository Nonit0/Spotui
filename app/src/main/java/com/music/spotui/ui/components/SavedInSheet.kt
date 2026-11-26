package com.music.spotui.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.metrolist.spotify.Spotify
import com.metrolist.spotify.models.SpotifyPlaylist
import com.music.spotui.R
import com.music.spotui.data.api.Api
import com.music.spotui.data.api.SpotifySync
import com.music.spotui.data.api.SpotifyTokenProvider
import com.music.spotui.data.entity.SongsModel
import com.music.spotui.data.preferences.LocalPlaylist
import com.music.spotui.data.preferences.LocalPlaylistPref
import com.music.spotui.data.preferences.addLikedSongId
import com.music.spotui.data.preferences.isSongLiked
import com.music.spotui.data.preferences.removeLikedSongId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

private val SpotifyGreen = Color(0xFF1ED760)

/**
 * Spotify-style "Saved in" sheet: Liked Songs plus local playlists and the user's own Spotify
 * playlists. It is the single flow behind every song heart button (IT-61): rows are checkboxes
 * that only stage a change; nothing is saved or removed until the user taps "Save". Dismissing
 * the sheet discards the staged changes. Creating a new playlist is the one immediate action.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun SavedInSheet(
    song: SongsModel,
    context: Context,
    onDismiss: () -> Unit,
    onLikedChanged: (Boolean) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val initialLiked = remember(song.id) { isSongLiked(context, song.id.toString()) }
    // Staged changes only: key → desired state, kept while it differs from the initial one.
    // Keys: "liked", "local:<id>", "remote:<id>".
    val pending = remember(song.id) { mutableStateMapOf<String, Boolean>() }
    val liked = pending["liked"] ?: initialLiked
    fun stage(key: String, initial: Boolean, current: Boolean) {
        if (!current == initial) pending.remove(key) else pending[key] = !current
    }
    var localPlaylists by remember { mutableStateOf<List<LocalPlaylist>>(emptyList()) }
    var remotePlaylists by remember { mutableStateOf<List<SpotifyPlaylist>?>(null) }
    // playlistId → does it contain this track (filled lazily/locally per row).
    val membership = remember(song.id) { mutableStateMapOf<String, Boolean>() }
    var creating by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isCreatingPlaylist by remember { mutableStateOf(false) }

    LaunchedEffect(song.id) {
        fun cleanId(id: String): String =
            id.removePrefix("spotify:playlist:")
              .removePrefix("playlist:")
              .trim()

        val locals = LocalPlaylistPref.getLocalPlaylists(context)
            .distinctBy { cleanId(it.id).ifBlank { it.name.trim().lowercase() } }
        localPlaylists = locals

        val localIds = locals.map { cleanId(it.id) }.filter { it.isNotBlank() }.toSet()
        val localNames = locals.map { it.name.trim().lowercase() }.toSet()

        remotePlaylists = withContext(Dispatchers.IO) {
            // Only playlists the user can write to: their own, plus other people's that
            // Spotify marks as editable for us (collaborative). (When the profile can't be
            // fetched we can't tell, so keep them all and let a failed add report the error.)
            val myId = SpotifyTokenProvider.ensureToken(context.applicationContext)
                .let { if (it) Spotify.me().getOrNull()?.id else null }
            val all = Spotify.myPlaylists().getOrNull()?.items
                ?.filter { it.id.isNotBlank() }
                ?: emptyList()
            fun isMine(p: SpotifyPlaylist) = myId == null || p.owner?.id.isNullOrBlank() || p.owner?.id == myId
            // Someone else's playlist the library listing didn't classify: ask its detail
            // (fetchPlaylist carries the capabilities / members), a few at a time.
            val unknown = all.filter { !isMine(it) && it.canEdit == null && !it.collaborative }
            val resolved: Map<String, Boolean> = coroutineScope {
                unknown.chunked(4).flatMap { batch ->
                    batch.map { p ->
                        async {
                            val d = Spotify.playlist(p.id).getOrNull()
                            p.id to (d?.canEdit ?: d?.collaborative ?: false)
                        }
                    }.awaitAll()
                }.toMap()
            }
            val items = all.filter { p ->
                isMine(p) || p.canEdit ?: (p.collaborative || resolved[p.id] == true)
            }
            items.distinctBy { cleanId(it.id).ifBlank { it.name.trim().lowercase() } }
                .filterNot { item ->
                    val cid = cleanId(item.id)
                    (cid.isNotBlank() && localIds.contains(cid)) || localNames.contains(item.name.trim().lowercase())
                }
        }
    }

    fun createNow() {
        val name = newName.trim().ifBlank { "My Playlist" }
        isCreatingPlaylist = true
        newName = ""
        scope.launch {
            // On Spotify itself first; a local playlist only if that fails.
            val remote = if (song.spotifyTrackId.isBlank()) null
                else withContext(Dispatchers.IO) { SpotifySync.createPlaylist(context, name, song.spotifyTrackId) }
            if (remote != null) {
                remotePlaylists = listOf(remote) + remotePlaylists.orEmpty()
                android.widget.Toast.makeText(context, "Playlist '$name' created on Spotify", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                val created = LocalPlaylistPref.createPlaylist(context, name, song)
                localPlaylists = LocalPlaylistPref.getLocalPlaylists(context)
                membership[created.id] = true
                Api.HomeCache.library = null
                android.widget.Toast.makeText(context, "Couldn't reach Spotify — '$name' created as a local playlist", android.widget.Toast.LENGTH_SHORT).show()
            }
            creating = false
            isCreatingPlaylist = false
        }
    }

    fun applyNow() {
        val changes = pending.toMap()
        val songId = song.id.toString()
        changes.forEach { (key, want) ->
            when {
                key == "liked" -> {
                    if (want) addLikedSongId(context, songId) else removeLikedSongId(context, songId)
                    SpotifySync.setTrackSaved(context, song.spotifyTrackId, want)
                }
                key.startsWith("local:") -> {
                    val id = key.removePrefix("local:")
                    if (want) LocalPlaylistPref.addSongToPlaylist(context, id, song)
                    else LocalPlaylistPref.removeSongFromPlaylist(context, id, song.id, song.spotifyTrackId)
                    Api.HomeCache.library = null
                }
                key.startsWith("remote:") -> {
                    val id = key.removePrefix("remote:")
                    val done: (Boolean) -> Unit = { ok ->
                        if (!ok) android.os.Handler(android.os.Looper.getMainLooper()).post {
                            android.widget.Toast.makeText(context, "Couldn't update a Spotify playlist", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (want) SpotifySync.addTrackToPlaylist(context, id, song.spotifyTrackId, done)
                    else SpotifySync.removeTrackFromPlaylist(context, id, song.spotifyTrackId, done)
                }
            }
        }
        if (changes.containsKey("liked")) onLikedChanged(changes.getValue("liked"))
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A),
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 4.dp, 20.dp, 12.dp),
            ) {
                Text("Saved in", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "New playlist",
                    color = SpotifyGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(
                        enabled = !isCreatingPlaylist,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { creating = true },
                )
            }

            if (creating) {
                TextField(
                    enabled = !isCreatingPlaylist,
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    placeholder = { Text("Playlist name", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { createNow() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = SpotifyGreen,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 0.dp, 20.dp, 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Row(modifier = Modifier.padding(20.dp, 4.dp, 20.dp, 12.dp)) {
                    Text(
                        if (isCreatingPlaylist) "Creating..." else "Create",
                        color = if (isCreatingPlaylist) Color.Gray else SpotifyGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                            enabled = !isCreatingPlaylist,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { createNow() },
                    )
                    Spacer(Modifier.width(24.dp))
                    Text(
                        "Cancel",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable(
                            enabled = !isCreatingPlaylist,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { creating = false; newName = "" },
                    )
                }
            }

            LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                // ── Liked Songs ──
                item {
                    SavedInRow(
                        name = "Liked Songs",
                        subtitle = "Liked tracks",
                        saved = liked,
                        isLocal = false,
                        cover = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFF4A39EA), Color(0xFF868AE1)))
                                    ),
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        },
                    ) {
                        stage("liked", initialLiked, liked)
                    }
                }

                // ── Local Playlists ──
                items(localPlaylists.size, key = { "local_${localPlaylists[it].id}" }) { index ->
                    val pl = localPlaylists[index]
                    val initial = LocalPlaylistPref.isSongInPlaylist(context, pl.id, song.id, song.spotifyTrackId)
                    val saved = pending["local:${pl.id}"] ?: membership[pl.id] ?: initial
                    SavedInRow(
                        name = pl.name,
                        subtitle = "${pl.songs.size} song" + (if (pl.songs.size == 1) "" else "s"),
                        saved = saved,
                        isLocal = true,
                        cover = {
                            GlideImage(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                model = pl.coverUri,
                                contentScale = ContentScale.Crop,
                                failure = placeholder(R.drawable.placeholder),
                                loading = placeholder(R.drawable.placeholder),
                                contentDescription = "",
                            )
                        },
                    ) {
                        stage("local:${pl.id}", membership[pl.id] ?: initial, saved)
                    }
                }

                // ── Remote Spotify Playlists ──
                val remoteList = remotePlaylists.orEmpty()
                items(remoteList.size, key = { "remote_${remoteList[it].id}" }) { i ->
                    val pl = remoteList[i]
                    LaunchedEffect(pl.id, song.spotifyTrackId) {
                        if (membership[pl.id] == null && song.spotifyTrackId.isNotBlank()) {
                            membership[pl.id] = withContext(Dispatchers.IO) {
                                SpotifySync.playlistTrackIds(context, pl.id).contains(song.spotifyTrackId)
                            }
                        }
                    }
                    val saved = pending["remote:${pl.id}"] ?: (membership[pl.id] == true)
                    SavedInRow(
                        name = pl.name,
                        subtitle = pl.tracks?.total?.let { n -> "$n song" + (if (n == 1) "" else "s") } ?: "",
                        saved = saved,
                        isLocal = false,
                        cover = {
                            GlideImage(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                model = pl.images.firstOrNull()?.url,
                                contentScale = ContentScale.Crop,
                                failure = placeholder(R.drawable.placeholder),
                                loading = placeholder(R.drawable.placeholder),
                                contentDescription = "",
                            )
                        },
                    ) {
                        // Ignore taps until we know whether the playlist already has the track.
                        val initial = membership[pl.id] ?: return@SavedInRow
                        if (song.spotifyTrackId.isBlank()) return@SavedInRow
                        stage("remote:${pl.id}", initial, saved)
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 12.dp, 20.dp, 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (pending.isEmpty()) Color(0xFF2A2A2A) else SpotifyGreen)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { applyNow() }
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    if (pending.isEmpty()) "Done" else "Save",
                    color = if (pending.isEmpty()) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SavedInRow(
    name: String,
    subtitle: String,
    saved: Boolean,
    isLocal: Boolean = false,
    cover: @Composable () -> Unit,
    onToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onToggle() }
            .padding(20.dp, 8.dp),
    ) {
        cover()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 8.dp),
        ) {
            Text(name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLocal) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Local Storage",
                        tint = SpotifyGreen,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(end = 3.dp)
                    )
                }
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        if (saved) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Saved", tint = SpotifyGreen, modifier = Modifier.size(26.dp))
        } else {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFB3B3B3), modifier = Modifier.size(26.dp))
        }
    }
}
