package com.example.app.features

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.app.ui.theme.GreenColor

// Define custom colors for the green and orange theme

@Composable
fun BottomNavBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .shadow(4.dp), // Add shadow to the navigation bar
        containerColor = GreenColor, // Use green as the background color
        contentColor = Color.White // Use white for text and icons
    ) {
        NavigationBarItem(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home") },
        )
        NavigationBarItem(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help Center", tint = Color.White) },
            label = { Text("Help") },
        )
        NavigationBarItem(
            selected = selectedTabIndex == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Filled.VideoLibrary, contentDescription = "Video Tutorials", tint = Color.White) },
            label = { Text("Videos") },
        )
        NavigationBarItem(
            selected = selectedTabIndex == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "User  Profile", tint = Color.White) },
            label = { Text("Profile") },
        )
    }
}


