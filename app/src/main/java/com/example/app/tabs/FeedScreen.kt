package com.example.app.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import kotlinx.coroutines.launch

// ---------------- Video data ----------------
data class VideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val views: String,
    val time: String,
    val thumbnailRes: Int
)

val sampleVideos = listOf(
    VideoItem("1", "How to Identify Common Livestock Diseases", "FarmHelp Kenya", "12K views", "3 days ago", R.drawable.ic_launcher_background),
    VideoItem("2", "Effective Pest Control for Maize Farmers", "Agri Solutions", "30K views", "1 week ago", R.drawable.ic_launcher_background),
    VideoItem("3", "Feeding Guide for Dairy Cows in Kenya", "VetAdvice KE", "7.8K views", "2 weeks ago", R.drawable.ic_launcher_background),
    VideoItem("4", "Organic Farming Tips to Boost Yields", "GreenFarmers TV", "21K views", "5 days ago", R.drawable.ic_launcher_background),
    VideoItem("5", "How to Diagnose Soil Nutrient Deficiencies", "SoilCare Africa", "18K views", "6 days ago", R.drawable.ic_launcher_background),
    VideoItem("6", "Best Chicken Breeds for Small Scale Farming", "FarmHub Tutorials", "10.2K views", "4 days ago", R.drawable.ic_launcher_background)
)

val videoFilters = listOf("All", "Livestock", "Crops", "Dairy", "Organic", "Soil", "Poultry")

// ---------------- Main responsive screen ----------------
@Composable
fun VideoScreen() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLargeScreen = maxWidth > 600.dp
        if (isLargeScreen) {
            PermanentDrawerScreen()
        } else {
            ModalDrawerScreen()
        }
    }
}

// ---------------- Modal Drawer for small screens ----------------
@Composable
fun ModalDrawerScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(MaterialTheme.colorScheme.surface)  // panel background
                    .padding(16.dp)
            ) {
                Text("Categories", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                videoFilters.forEach { filter ->
                    Text(
                        filter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFilter = filter
                                scope.launch { drawerState.close() }
                            }
                            .padding(vertical = 16.dp)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Horizontal row: drawer button + filters
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Categories")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterRow(selectedFilter = selectedFilter) { selectedFilter = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                VideoFeed(selectedFilter = selectedFilter)
            }
        }
    }
}

// ---------------- Permanent Drawer for large screens ----------------
@Composable
fun PermanentDrawerScreen() {
    var selectedFilter by remember { mutableStateOf("All") }

    PermanentNavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(MaterialTheme.colorScheme.surface) // panel background
                    .padding(16.dp)
            ) {
                Text("Categories", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                videoFilters.forEach { filter ->
                    Text(
                        filter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFilter = filter }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Horizontal row: optional placeholder icon + filters
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                IconButton(onClick = { /* optional: scroll or other action */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Categories")
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilterRow(selectedFilter = selectedFilter) { selectedFilter = it }
            }

            Spacer(modifier = Modifier.height(12.dp))

            VideoFeed(selectedFilter = selectedFilter)
        }
    }
}

// ---------------- Horizontal filter row ----------------
@Composable
fun FilterRow(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoFilters) { filter ->
            val isSelected = filter == selectedFilter
            Text(
                text = filter,
                modifier = Modifier
                    .clickable { onFilterSelected(filter) }
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ---------------- Video feed ----------------
@Composable
fun VideoFeed(selectedFilter: String) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        val filteredVideos = if (selectedFilter == "All") sampleVideos
        else sampleVideos.filter {
            it.title.contains(selectedFilter, ignoreCase = true) ||
                    it.channel.contains(selectedFilter, ignoreCase = true)
        }

        items(filteredVideos) { video ->
            VideoCard(video)
        }
    }
}

// ---------------- Video card ----------------
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
                Text(
                    "${video.channel} • ${video.views} • ${video.time}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
