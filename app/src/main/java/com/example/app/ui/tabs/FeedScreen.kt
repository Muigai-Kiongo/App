package com.example.app.ui.tabs

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.app.ui.components.FilterRow
import com.example.app.ui.components.VideoCard
import com.example.app.features.HomeTab
import com.example.app.models.VideoViewModel
import com.example.app.models.videoFilters
import kotlinx.coroutines.launch

@Composable
fun VideoScreen(navController: NavHostController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLargeScreen = maxWidth > 600.dp
        if (isLargeScreen) {
            PermanentDrawerScreen(navController)
        } else {
            ModalDrawerScreen(navController)
        }
    }
}

@Composable
fun ModalDrawerScreen(navController: NavHostController) {
    var selectedFilter by remember { mutableStateOf("All") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                    scope.launch { drawerState.close() }
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Categories")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterRow(
                        selectedFilter = selectedFilter,
                        filters = videoFilters
                    ) { selectedFilter = it }
                }

                Spacer(modifier = Modifier.height(12.dp))
                VideoFeed(
                    selectedFilter = selectedFilter,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun PermanentDrawerScreen(navController: NavHostController) {
    var selectedFilter by remember { mutableStateOf("All") }

    PermanentNavigationDrawer(
        drawerContent = {
            DrawerContent(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                IconButton(onClick = { /* optional */ }) {
                    Icon(Icons.Default.Menu, contentDescription = "Categories")
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilterRow(
                    selectedFilter = selectedFilter,
                    filters = videoFilters
                ) { selectedFilter = it }
            }

            Spacer(modifier = Modifier.height(12.dp))
            VideoFeed(
                selectedFilter = selectedFilter,
                navController = navController
            )
        }
    }
}

@Composable
fun DrawerContent(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        videoFilters.forEach { filter ->
            Text(
                filter,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 12.dp),
                color = if (filter == selectedFilter)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun VideoFeed(
    selectedFilter: String,
    navController: NavHostController,
    videoViewModel: VideoViewModel = viewModel()
) {
    val videos = videoViewModel.videos
    val filteredVideos = if (selectedFilter == "All") videos
    else videos.filter {
        it.title.contains(selectedFilter, ignoreCase = true) ||
                it.channel.contains(selectedFilter, ignoreCase = true)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        items(filteredVideos) { video ->
            VideoCard(video = video) {
                navController.navigate(HomeTab.VideoDetail.createRoute(video.id))
            }
        }
    }
}
