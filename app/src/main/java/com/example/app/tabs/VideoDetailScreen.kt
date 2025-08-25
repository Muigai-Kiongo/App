package com.example.app.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------- Sample comments ----------------
data class Comment(val user: String, val text: String)

val sampleComments = listOf(
    Comment("Alice", "Great tutorial! Learned a lot."),
    Comment("Bob", "Thanks for sharing, very helpful."),
    Comment("Charlie", "Can you make a video on organic fertilizers?"),
    Comment("Daisy", "Loved the explanation, clear and concise."),
    Comment("Eve", "More videos like this please!")
)

// ---------------- Video Detail Screen ----------------
@Composable
fun VideoDetailScreen(video: VideoItem, relatedVideos: List<VideoItem> = sampleVideos) {
    var commentsExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        // Video title / info
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(video.title, fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${video.channel} • ${video.views} • ${video.time}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }

        // ---------------- Expandable Comments Card ----------------
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {

                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { commentsExpanded = !commentsExpanded }
                    ) {
                        Text("Comments (${sampleComments.size})", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (commentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (commentsExpanded) "Collapse" else "Expand"
                        )
                    }

                    if (commentsExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        sampleComments.forEach { comment ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(comment.user, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Text(comment.text, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // ---------------- Related Videos ----------------
        item {
            Text("Related Videos", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(relatedVideos) { videoItem ->
            VideoCard(videoItem)
        }
    }
}
