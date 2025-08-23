package com.example.app.features

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.GreenColor

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            route = HomeTab.Intro.route,
            label = "Home",
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }
        ),
        BottomNavItem(
            route = HomeTab.Help.route,
            label = "Help",
            icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help Center") }
        ),
        BottomNavItem(
            route = HomeTab.Video.route,
            label = "Videos",
            icon = { Icon(Icons.Filled.VideoLibrary, contentDescription = "Video Tutorials") }
        ),
        BottomNavItem(
            route = HomeTab.Profile.route,
            label = "Profile",
            icon = { Icon(Icons.Filled.Person, contentDescription = "User Profile") }
        )
    )

    NavigationBar(
        modifier = modifier.shadow(4.dp),
        containerColor = GreenColor,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onTabSelected(item.route) },
                icon = item.icon,
                label = { Text(item.label) }
            )
        }
    }
}
