package com.example.app.features

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.routes.IntroScreen
import com.example.app.tabs.ChatScreen
import com.example.app.tabs.HelpScreen
import com.example.app.tabs.ProfileScreen
import com.example.app.tabs.VideoScreen

fun getTitleForRoute(route: String?): String {
    return when (route) {
        HomeTab.Intro.route -> "Farm Hub"
        HomeTab.Help.route -> "Help Center"
        HomeTab.Video.route -> "Video Tutorials"
        HomeTab.Profile.route -> "Profile"
        HomeTab.Chat.route -> "Chat"
        else -> "Farm Hub"
    }
}

@Composable
fun AppNavigation(onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = HomeTab.Intro.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(HomeTab.Intro.route) {
            IntroScreen(
                onFarmHelpClick = { navController.navigate(HomeTab.Help.route) { popUpTo(HomeTab.Intro.route) { inclusive = true } } },
                onLearnMoreClick = { navController.navigate(HomeTab.Help.route) },
                onSignupClick = { }
            )
        }

        composable(HomeTab.Help.route) {
            ScaffoldWrapper(navController, currentRoute) { HelpScreen() }
        }
        composable(HomeTab.Video.route) {
            ScaffoldWrapper(navController, currentRoute) { VideoScreen() }
        }
        composable(HomeTab.Profile.route) {
            ScaffoldWrapper(navController, currentRoute) {
                ProfileScreen(onToggleTheme = onToggleTheme)
            }
        }
        composable(HomeTab.Chat.route) {
            ScaffoldWrapper(navController, currentRoute) { ChatScreen() }
        }
    }
}


@Composable
fun ScaffoldWrapper(
    navController: androidx.navigation.NavHostController,
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = getTitleForRoute(currentRoute),
                onChatClick = { navController.navigate(HomeTab.Chat.route) },
                onSearchClick = { /* TODO: add search later */ }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    navController.navigate(route) {

                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}
