package com.example.app.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

data class VideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val views: String,
    val time: String,
    val thumbnailRes: Int
)

val sampleVideos = listOf(
    VideoItem(
        id = "1",
        title = "How to Identify Common Livestock Diseases",
        channel = "FarmHelp Kenya",
        views = "12K views",
        time = "3 days ago",
        thumbnailRes = R.drawable.ic_launcher_background // Replace with actual farm-related image
    ),
    VideoItem(
        id = "2",
        title = "Effective Pest Control for Maize Farmers",
        channel = "Agri Solutions",
        views = "30K views",
        time = "1 week ago",
        thumbnailRes = R.drawable.ic_launcher_background
    ),
    VideoItem(
        id = "3",
        title = "Feeding Guide for Dairy Cows in Kenya",
        channel = "VetAdvice KE",
        views = "7.8K views",
        time = "2 weeks ago",
        thumbnailRes = R.drawable.ic_launcher_background
    ),
    VideoItem(
        id = "4",
        title = "Organic Farming Tips to Boost Yields",
        channel = "GreenFarmers TV",
        views = "21K views",
        time = "5 days ago",
        thumbnailRes = R.drawable.ic_launcher_background
    ),
    VideoItem(
        id = "5",
        title = "How to Diagnose Soil Nutrient Deficiencies",
        channel = "SoilCare Africa",
        views = "18K views",
        time = "6 days ago",
        thumbnailRes = R.drawable.ic_launcher_background
    ),
    VideoItem(
        id = "6",
        title = "Best Chicken Breeds for Small Scale Farming",
        channel = "FarmHub Tutorials",
        views = "10.2K views",
        time = "4 days ago",
        thumbnailRes = R.drawable.ic_launcher_background
    )
)


@Composable
fun VideoScreen() {
    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            items(sampleVideos) { video ->
                VideoCard(video)
            }
        }
    }
}

@Composable
fun VideoCard(video: VideoItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* navigate to video detail */ }
    ) {
        Image(
            painter = painterResource(id = video.thumbnailRes),
            contentDescription = video.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                modifier = Modifier.padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(video.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${video.channel} • ${video.views} • ${video.time}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
