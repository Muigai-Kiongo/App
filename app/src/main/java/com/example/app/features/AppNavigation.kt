package com.example.app.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.routes.AuthScreen
import com.example.app.routes.IntroScreen
import com.example.app.tabs.ChatScreen
import com.example.app.tabs.HelpScreen
import com.example.app.tabs.ProfileScreen
import com.example.app.tabs.VideoDetailScreen
import com.example.app.tabs.VideoScreen
import com.example.app.models.VideoViewModel
import com.example.app.components.AppHeader
import com.example.app.components.BottomNavBar

fun getTitleForRoute(route: String?): String {
    return when {
        route == null -> "Farm Hub"
        route == HomeTab.Intro.route -> "Farm Hub"
        route == HomeTab.Help.route -> "Help Center"
        route == HomeTab.Video.route -> "Video Tutorials"
        route == HomeTab.Profile.route -> "Profile"
        route == HomeTab.Chat.route -> "Chat"
        else -> "Farm Hub"
    }
}

@Composable
fun AppNavigation(onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Shared ViewModel across screens
    val videoViewModel: VideoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = HomeTab.Intro.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // Intro
        composable(HomeTab.Intro.route) {
            IntroScreen(
                onFarmHelpClick = {
                    navController.navigate(HomeTab.Help.route) {
                        popUpTo(HomeTab.Intro.route) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(HomeTab.Auth.route) }
            )
        }

        // Auth
        composable(HomeTab.Auth.route) {
            AuthScreen(
                onLoginSuccess = { /* navigate somewhere after login */ }
            )
        }

        // Help
        composable(HomeTab.Help.route) {
            ScaffoldWrapper(navController, currentRoute) { HelpScreen() }
        }

        // Video Feed
        composable(HomeTab.Video.route) {
            ScaffoldWrapper(navController, currentRoute) {
                VideoScreen(
                    navController = navController,
                )
            }
        }

        // Video Detail with comments + related videos
        composable(
            route = HomeTab.VideoDetail.route
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId")?.toIntOrNull()
            val video = videoId?.let { videoViewModel.getVideoById(it) }

            if (video != null) {
                ScaffoldWrapper(navController, currentRoute) {
                    VideoDetailScreen(
                        videoId = videoId,
                        videoViewModel = videoViewModel,
                        onVideoClick = { selectedId ->
                            navController.navigate("videoDetail/$selectedId")
                        }
                    )

                }
            }
        }


        // Profile
        composable(HomeTab.Profile.route) {
            ScaffoldWrapper(navController, currentRoute) {
                ProfileScreen(onToggleTheme = onToggleTheme)
            }
        }

        // Chat
        composable(HomeTab.Chat.route) {
            ScaffoldWrapper(navController, currentRoute) { ChatScreen() }
        }
    }
}

@Composable
fun ScaffoldWrapper(
    navController: NavHostController,
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = getTitleForRoute(currentRoute),
                onChatClick = { navController.navigate(HomeTab.Chat.route) },
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}
