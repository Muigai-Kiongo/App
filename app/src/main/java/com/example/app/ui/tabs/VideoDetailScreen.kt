package com.example.app.ui.tabs

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.models.media.MediaItem
import com.example.app.viewmodel.MediaUiState
import com.example.app.viewmodel.MediaViewModel
import androidx.media3.common.MediaItem as Media3MediaItem

@Composable
fun KeepScreenOn(keepOn: Boolean) {
    val activity = LocalActivity.current
    DisposableEffect(keepOn) {
        if (keepOn) {
            activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun VideoDetailScreen(
    videoId: String,
    viewModel: MediaViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController? = null,
    isFullscreen: Boolean,
    setFullscreen: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Use optimized playback position from ViewModel
    var currentPosition by rememberSaveable(videoId) {
        androidx.compose.runtime.mutableLongStateOf(viewModel.getPlaybackPosition(videoId))
    }

    // Get Activity for immersive UI/orientation
    val activity = LocalActivity.current

    // Lock/unlock orientation on fullscreen, and handle immersive system UI
    LaunchedEffect(isFullscreen) {
        activity?.let {
            it.requestedOrientation = if (isFullscreen)
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            val window = it.window
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            if (isFullscreen) {
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Keep the screen on when fullscreen video is playing
    KeepScreenOn(isFullscreen)

    // BackHandler to exit fullscreen with system back
    if (isFullscreen) {
        BackHandler { setFullscreen(false) }
    }

    when (uiState) {
        is MediaUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is MediaUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((uiState as MediaUiState.Error).message)
            }
        }
        is MediaUiState.Success -> {
            val videos = (uiState as MediaUiState.Success).videos
            val video = videos.find { it.id == videoId }
            val relatedVideos = videos.filter { it.id != videoId }

            if (video == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Video not found")
                }
                return
            }

            Surface {
                if (isFullscreen) {
                    // Show only the video in fullscreen mode
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        VideoPlayerMedia3(
                            video = video,
                            isFullscreen = true,
                            onFullscreenToggle = { setFullscreen(false) },
                            playbackPosition = currentPosition,
                            onPositionChanged = { pos ->
                                currentPosition = pos
                                viewModel.setPlaybackPosition(videoId, pos)
                                viewModel.setLastPlayedVideoId(videoId)
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp)
                    ) {
                        item {
                            VideoPlayerMedia3(
                                video = video,
                                isFullscreen = false,
                                onFullscreenToggle = { setFullscreen(true) },
                                playbackPosition = currentPosition,
                                onPositionChanged = { pos ->
                                    currentPosition = pos
                                    viewModel.setPlaybackPosition(videoId, pos)
                                    viewModel.setLastPlayedVideoId(videoId)
                                }
                            )
                        }
                        item {
                            VideoDetailsSection(video)
                        }
                        item {
                            HorizontalDivider(
                                Modifier.padding(vertical = 12.dp),
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )
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
                            HorizontalDivider(
                                Modifier,
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerMedia3(
    video: MediaItem,
    isFullscreen: Boolean,
    onFullscreenToggle: () -> Unit,
    playbackPosition: Long,
    onPositionChanged: (Long) -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember(video.video) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(Media3MediaItem.fromUri(video.video))
            playWhenReady = true
            prepare()
        }
    }
    // Seek to remembered position when player is (re)created
    LaunchedEffect(exoPlayer, playbackPosition) {
        if (playbackPosition > 0) {
            exoPlayer.seekTo(playbackPosition)
        }
    }
    // Update playback position as it changes
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(context, "ExoPlayer error: ${error.message}", Toast.LENGTH_LONG).show()
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    onPositionChanged(exoPlayer.currentPosition)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            onPositionChanged(exoPlayer.currentPosition)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            },
            modifier = Modifier.matchParentSize()
        )
        // Fullscreen button (top right)
        IconButton(
            onClick = onFullscreenToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                tint = Color.White
            )
        }
    }
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
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "No thumbnail",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            // Play icon overlay
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(4.dp)
            )
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