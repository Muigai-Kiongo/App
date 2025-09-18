package com.example.app.ui.tabs

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.viewmodel.MediaViewModel
import com.example.app.models.media.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem as ExoMediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale

@Composable
fun VideoDetailScreen(
    videoId: String,
    viewModel: MediaViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController? = null // navController is only needed for related video navigation
) {
    val uiState by viewModel.uiState.collectAsState()
    val video = (uiState as? com.example.app.viewmodel.MediaUiState.Success)
        ?.videos?.find { it.id == videoId }

    val relatedVideos = (uiState as? com.example.app.viewmodel.MediaUiState.Success)
        ?.videos?.filter { it.id != videoId }.orEmpty()

    if (video == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Video not found")
        }
        return
    }

    Surface {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            item {
                VideoPlayer(videoUrl = video.video)
            }
            item {
                VideoDetailsSection(video)
            }
            item {
                Divider(Modifier.padding(vertical = 12.dp))
            }
            item {
                Text(
                    text = "Related Videos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            items(relatedVideos) { related ->
                RelatedVideoListItem(
                    video = related,
                    onClick = {
                        navController?.navigate("video_detail/${related.id}")
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = ExoMediaItem.fromUri(Uri.parse(videoUrl))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            StyledPlayerView(it).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    )
}

@Composable
fun VideoDetailsSection(video: MediaItem) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = video.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = video.company.ifBlank { "by FarmHub" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Posted: ${video.createdAt.substringBefore("T")}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dummy stats, replace with actual if available
            Text(
                text = "${video.views ?: 1234} views",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "${video.comments ?: 5} comments",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun RelatedVideoListItem(video: MediaItem, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(96.dp, 54.dp)
                .clip(MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            if (video.thumbnail.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Thumbnail for ${video.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                        contentDescription = "No thumbnail",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2
            )
            Text(
                text = video.company.ifBlank { "by FarmHub" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = video.createdAt.substringBefore("T"),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

// If your MediaItem model does not include views/comments, dummy values are shown.
// Replace below with actual properties if available.
val MediaItem.views: Int?
    get() = null // Replace with actual field

val MediaItem.comments: Int?
    get() = null // Replace with actual field