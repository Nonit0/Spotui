package com.music.spotui.di

import com.metrolist.innertube.models.Album
import com.metrolist.innertube.models.Artist
import com.metrolist.innertube.models.SongItem
import com.music.spotui.di.SongPlayer.TrackMatchMetadata
import com.music.spotui.di.TrackMatcher.isAcceptableMatch
import org.junit.Assert.*
import org.junit.Test

/**
 * Pins the current behaviour of [TrackMatcher]. android.icu's Transliterator is not
 * available in JVM tests (it resolves to null), so transliteration cases are not covered.
 */
class TrackMatcherTest {

    private val expected = TrackMatchMetadata(
        title = "Blinding Lights",
        artist = "The Weeknd",
        album = "After Hours",
    )
    private val expectedDurationMs = 200_000

    private fun song(
        title: String = "Blinding Lights",
        artist: String = "The Weeknd",
        album: String? = "After Hours",
        duration: Int? = 200,
        explicit: Boolean = false,
    ) = SongItem(
        id = "id",
        title = title,
        artists = listOf(Artist(name = artist, id = null)),
        album = album?.let { Album(name = it, id = "album") },
        duration = duration,
        thumbnail = "",
        explicit = explicit,
    )

    private fun score(candidate: SongItem) =
        TrackMatcher.ytmusicTransferScore(candidate, expected, expectedDurationMs)

    @Test
    fun normalizedForMatchDropsFeatAndPunctuation() {
        assertEquals("song", TrackMatcher.normalizedForMatch("Song (feat. X)!"))
        assertEquals("hello world", TrackMatcher.normalizedForMatch("  Hello,   World "))
    }

    @Test
    fun foldLatinDiacriticsRemovesAccents() {
        assertEquals("Beyonce", TrackMatcher.foldLatinDiacritics("Beyoncé"))
    }

    @Test
    fun bigramSimilarityIgnoresAccents() {
        assertEquals(1.0, TrackMatcher.bigramSimilarity("Beyoncé", "Beyonce"), 1e-9)
    }

    @Test
    fun bigramSimilarityIsLowForDifferentTitles() {
        assertEquals(1.0, TrackMatcher.bigramSimilarity("Blinding Lights", "blinding lights"), 1e-9)
        assertTrue(TrackMatcher.bigramSimilarity("Blinding Lights", "Save Your Tears") < 0.3)
    }

    @Test
    fun versionMarkersDetectsAlternateVersions() {
        assertTrue("live" in TrackMatcher.versionMarkers("Song (Live at Wembley)"))
        assertTrue("remix" in TrackMatcher.versionMarkers("Song - Remix"))
        assertTrue("acoustic" in TrackMatcher.versionMarkers("Song (Acoustic)"))
        assertTrue("remaster" in TrackMatcher.versionMarkers("Song (Remastered 2011)"))
        assertTrue(TrackMatcher.versionMarkers("Song").isEmpty())
    }

    @Test
    fun versionMarkersTellCensoredFromUncensored() {
        assertTrue("clean" in TrackMatcher.versionMarkers("Song (Censored)"))
        val uncensored = TrackMatcher.versionMarkers("Song (Uncensored)")
        assertTrue("explicit" in uncensored)
        assertFalse("clean" in uncensored)
    }

    @Test
    fun acceptsExactMatch() {
        val result = score(song())
        assertEquals(4.0, result.score, 1e-9)
        assertTrue(result.isAcceptableMatch())
    }

    @Test
    fun rejectsUnexpectedLiveVersion() {
        val result = score(song(title = "Blinding Lights (Live)"))
        assertEquals(listOf("live"), result.unexpectedAlternates)
        assertFalse(result.isAcceptableMatch())
    }

    @Test
    fun rejectsSameTitleByDifferentArtist() {
        val result = score(song(artist = "Taylor Swift"))
        assertFalse(result.isAcceptableMatch())
    }

    @Test
    fun softMarkerIsPenalisedButAccepted() {
        val result = score(song(title = "Blinding Lights (Remastered)"))
        assertEquals(listOf("remaster"), result.unexpectedAlternates)
        assertEquals(0.65, result.alternatePenalty, 1e-9)
        assertTrue(result.isAcceptableMatch())
    }

    @Test
    fun explicitMismatchRaisesTheBar() {
        // No album and a 65 s duration gap: score = (1 + 1 + 5 * 0.35) / 3 * 2 = 2.5,
        // above the normal minimum (2.25) but below the explicit-mismatch one (2.85).
        val result = score(song(album = null, duration = 265, explicit = false))
        assertEquals(2.5, result.score, 1e-9)
        assertTrue(result.isAcceptableMatch())
        assertTrue(result.isAcceptableMatch(wantExplicit = false))
        assertFalse(result.isAcceptableMatch(wantExplicit = true))
    }
}
