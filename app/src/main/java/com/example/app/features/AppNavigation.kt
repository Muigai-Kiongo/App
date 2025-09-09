package com.example.app.features

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.app.auth.AuthManager
import com.example.app.routes.AuthScreen
import com.example.app.routes.IntroScreen
import com.example.app.ui.tabs.HelpScreen
import com.example.app.ui.tabs.ProfileScreen
import com.example.app.ui.tabs.ChatScreen
import com.example.app.ui.tabs.VideoScreen
import com.example.app.ui.components.BottomNavBar
import com.example.app.ui.components.AppHeader
import com.example.app.ui.tabs.VideoDetailScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    navController: NavHostController,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn(context)) }

    // Track current route for bottom nav reactively
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoutes.INTRO

    // Observe auth state
    LaunchedEffect(Unit) {
        AuthManager.addAuthStateListener(context) { loggedIn ->
            isLoggedIn = loggedIn
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.INTRO
    ) {
        composable(AppRoutes.INTRO) {
            // Standalone: No header/bottom nav
            IntroScreen(
                onFarmHelpClick = { navController.navigate(AppRoutes.HELP) },
                onVideosClick = {
                    if (isLoggedIn) navController.navigate(AppRoutes.VIDEOS)
                    else navController.navigate(AppRoutes.AUTH)
                },
            )
        }
        composable(AppRoutes.AUTH) {
            // Standalone: No header/bottom nav
            AuthScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate(AppRoutes.INTRO) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                },
                onSignupSuccess = {
                    navController.navigate(AppRoutes.AUTH) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.HELP) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isLoggedIn = isLoggedIn,
                onToggleTheme = onToggleTheme
            ) {
                HelpScreen(
                    onChatClick = {
                        if (isLoggedIn) navController.navigate(AppRoutes.CHAT)
                        else navController.navigate(AppRoutes.AUTH)
                    },
                    onVideosClick = {
                        if (isLoggedIn) navController.navigate(AppRoutes.VIDEOS)
                        else navController.navigate(AppRoutes.AUTH)
                    }
                )
            }
        }
        composable(AppRoutes.PROFILE) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isLoggedIn = isLoggedIn,
                onToggleTheme = onToggleTheme
            ) {
                ProfileScreen(
                    onToggleTheme = onToggleTheme,
                    isLoggedIn = isLoggedIn,
                    onSignInClick = { navController.navigate(AppRoutes.AUTH) },
                    onSignOutClick = {
                        AuthManager.logout(context)
                        isLoggedIn = false
                        navController.navigate(AppRoutes.AUTH) {
                            popUpTo(AppRoutes.INTRO) { inclusive = false }
                        }
                    }
                )
            }
        }

        composable(AppRoutes.VIDEOS) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isLoggedIn = isLoggedIn,
                onToggleTheme = onToggleTheme
            ) {
                if (isLoggedIn) {
                    VideoScreen(navController = navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppRoutes.AUTH)
                    }
                }
            }
        }

        composable(
            route = "video_detail/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId") ?: return@composable
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isLoggedIn = isLoggedIn,
                onToggleTheme = onToggleTheme
            ) {
                VideoDetailScreen(
                    videoId = videoId,
                    onVideoClick = { nextId ->
                        navController.navigate("video_detail/$nextId")
                    }
                )
            }
        }

        composable(AppRoutes.CHAT) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isLoggedIn = isLoggedIn,
                onToggleTheme = onToggleTheme
            ) {
                if (isLoggedIn) {
                    ChatScreen()
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(AppRoutes.AUTH)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppScaffold(
    navController: NavHostController,
    currentRoute: String,
    isLoggedIn: Boolean,
    onToggleTheme: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = "Farm Hub",
                onChatClick = {
                    if (isLoggedIn) navController.navigate(AppRoutes.CHAT)
                    else navController.navigate(AppRoutes.AUTH)
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}