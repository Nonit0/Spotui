package com.music.spotui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.spotui.ui.theme.AppBackground

// Podcast episodes can't be played yet (no Spotify web player, and YouTube only
// finds unrelated audio — IT-57/IT-58), so podcast screens say so instead of
// listing episodes that do nothing. Nothing is fetched: the show REST calls hit 429.
@Composable
fun ShowScreen(navController: NavController, showId: String, showName: String = "") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(AppBackground.toArgb()))
            .statusBarsPadding(),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            tint = Color.White,
            contentDescription = "Back",
            modifier = Modifier
                .padding(16.dp)
                .size(26.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { navController.navigateUp() },
        )
        if (showName.isNotBlank()) {
            Text(
                text = showName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 48.dp),
            )
        }
        PodcastsUnavailableNotice()
    }
}

/** Shared "can't play podcasts" notice: Home's Podcasts chip, a show, the Podcasts category. */
@Composable
fun PodcastsUnavailableNotice(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Podcasts not available yet",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Episodes can't be played in Spotui for now.",
            color = Color(0xFFB3B3B3),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}
