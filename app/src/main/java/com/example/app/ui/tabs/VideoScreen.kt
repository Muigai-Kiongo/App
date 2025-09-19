package com.example.app.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.models.media.MediaItem
import com.example.app.viewmodel.MediaUiState
import com.example.app.viewmodel.MediaViewModel

@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: MediaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSidePanel by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.loadMedia()
    }

    Scaffold(
        topBar = {},
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Filters and SidePanel Toggle Row
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp), // Height for filter bar
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToggleSidePanelButton(
                        showSidePanel = showSidePanel,
                        onToggle = { showSidePanel = !showSidePanel }
                    )
                    FilterRow(
                        selected = selectedFilter,
                        onSelected = { selectedFilter = it }
                    )
                }
                Row(Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = showSidePanel,
                        enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(350)) + fadeOut()
                    ) {
                        SidePanel(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(180.dp)
                                .padding(end = 8.dp),
                            onHide = { showSidePanel = false }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (uiState) {
                            is MediaUiState.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is MediaUiState.Error -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = (uiState as MediaUiState.Error).message)
                                }
                            }
                            is MediaUiState.Success -> {
                                val videos = (uiState as MediaUiState.Success).videos
                                val filteredVideos = when (selectedFilter) {
                                    "Recent" -> videos.sortedByDescending { it.createdAt }
                                    "Company" -> videos.filter { it.company.isNotEmpty() }
                                    else -> videos
                                }
                                LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    items(filteredVideos) { video ->
                                        VideoListItem(
                                            video = video,
                                            onClick = { navController.navigate("video_detail/${video.id}") }
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
        }
    )
}

@Composable
fun FilterRow(
    selected: String,
    onSelected: (String) -> Unit
) {
    val filters = listOf("All", "Recent", "Company")
    Row(
        Modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            FilterChip(
                label = filter,
                selected = selected == filter,
                onClick = { onSelected(filter) }
            )
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(end = 4.dp)
            .clickable { onClick() }
    ) {
        Box(
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ToggleSidePanelButton(showSidePanel: Boolean, onToggle: () -> Unit) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier
            .padding(end = 4.dp)
            .size(36.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = if (showSidePanel) "Hide panel" else "Show panel"
        )
    }
}

@Composable
fun SidePanel(modifier: Modifier = Modifier, onHide: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
        modifier = modifier
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                IconButton(onClick = onHide) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Hide panel")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Home", modifier = Modifier.clickable { /* blank */ })
            Spacer(Modifier.height(8.dp))
            Text("Trending", modifier = Modifier.clickable { /* blank */ })
            Spacer(Modifier.height(8.dp))
            Text("Subscriptions", modifier = Modifier.clickable { /* blank */ })
            Spacer(Modifier.height(8.dp))
            Text("Library", modifier = Modifier.clickable { /* blank */ })
        }
    }
}

@Composable
fun VideoListItem(video: MediaItem, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            // Playback icon overlay (always visible, even with a thumbnail)
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Channel/User avatar placeholder
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
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
        Spacer(modifier = Modifier.height(8.dp))
        if (video.description.isNotBlank()) {
            Text(
                text = video.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}