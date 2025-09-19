package com.example.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.features.AppRoutes
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
            route = AppRoutes.HELP,
            label = "Help",
            icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help Center") }
        ),
        BottomNavItem(
            route = AppRoutes.CHAT,
            label = "Chat",
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") }
        ),
        BottomNavItem(
            route = AppRoutes.VIDEOS,
            label = "Videos",
            icon = { Icon(Icons.Filled.VideoLibrary, contentDescription = "Videos") }
        ),
        BottomNavItem(
            route = AppRoutes.PROFILE,
            label = "Profile",
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") }
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