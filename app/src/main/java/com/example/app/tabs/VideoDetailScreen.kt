package com.example.app.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.components.VideoCard
import com.example.app.models.VideoViewModel

// ---------------- Video Detail Screen ----------------
@Composable
fun VideoDetailScreen(
    videoId: Int,
    videoViewModel: VideoViewModel = viewModel(),
    onVideoClick: (Int) -> Unit
) {
    var commentsExpanded by remember { mutableStateOf(false) }

    // Fetch current video and related videos from ViewModel
    val video = videoViewModel.getVideoById(videoId)
    val relatedVideos = videoViewModel.getRelatedVideos(videoId)
    val comments = videoViewModel.comments

    if (video == null) {
        // Fallback if video not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Video not found", fontSize = 18.sp)
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Video title / info
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = video.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.channel} • ${video.views} • ${video.time}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // ---------------- Expandable Comments Card ----------------
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { commentsExpanded = !commentsExpanded }
                    ) {
                        Text(
                            "Comments (${comments.size})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (commentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (commentsExpanded) "Collapse" else "Expand"
                        )
                    }

                    if (commentsExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        comments.forEach { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(comment.user, fontWeight = FontWeight.Bold)
                                Text(comment.text, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // ---------------- Related Videos ----------------
        item {
            Text(
                "Related Videos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(relatedVideos) { videoItem ->
            VideoCard(
                video = videoItem,
                onClick = { onVideoClick(videoItem.id) }
            )
        }
    }
}
