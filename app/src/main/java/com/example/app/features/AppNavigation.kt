package com.example.app.features

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.app.auth.AuthManager
import com.example.app.routes.AuthScreen
import com.example.app.routes.IntroScreen
import com.example.app.ui.components.AppHeader
import com.example.app.ui.components.BottomNavBar
import com.example.app.ui.tabs.ChatScreen
import com.example.app.ui.tabs.HelpScreen
import com.example.app.ui.tabs.ProfileScreen
import com.example.app.ui.tabs.VideoDetailScreen
import com.example.app.ui.tabs.VideoScreen
import com.example.app.viewmodel.FeedViewModel
import com.example.app.viewmodel.MessageViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    navController: NavHostController,
    onToggleTheme: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn(context)) }
    var isVideoFullscreen by rememberSaveable { mutableStateOf(false) }

    // On app start, ensure UserSession is populated if session exists
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            AuthManager.loadSession(context)
        }
    }

    // Track current route for bottom nav reactively
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoutes.INTRO

    // Track intended destination after login
    var destinationAfterLogin by rememberSaveable { mutableStateOf<String?>(null) }
    // Track which card was clicked before login
    var postLoginAction by rememberSaveable { mutableStateOf<String?>(null) }

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
            IntroScreen(
                onFarmHelpClick = {
                    if (isLoggedIn) {
                        navController.navigate(AppRoutes.HELP)
                    } else {
                        destinationAfterLogin = AppRoutes.HELP
                        postLoginAction = "farm_help"
                        navController.navigate(AppRoutes.AUTH)
                    }
                },
                onVideosClick = {
                    if (isLoggedIn) {
                        navController.navigate(AppRoutes.VIDEOS)
                    } else {
                        destinationAfterLogin = AppRoutes.VIDEOS
                        postLoginAction = "farm_videos"
                        navController.navigate(AppRoutes.AUTH)
                    }
                },
            )
        }
        composable(AppRoutes.AUTH) {
            AuthScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    // Enhanced navigation logic:
                    val dest = when (postLoginAction) {
                        "farm_help" -> AppRoutes.HELP
                        "farm_videos" -> AppRoutes.VIDEOS
                        else -> destinationAfterLogin ?: AppRoutes.INTRO
                    }
                    destinationAfterLogin = null
                    postLoginAction = null
                    navController.navigate(dest) {
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
                isFullscreen = false
            ) {
                if (isLoggedIn) {
                    HelpScreen()
                } else {
                    destinationAfterLogin = AppRoutes.HELP
                    postLoginAction = "farm_help"
                    LaunchedEffect(Unit) { navController.navigate(AppRoutes.AUTH) }
                }
            }
        }
        composable(AppRoutes.PROFILE) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isFullscreen = false
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
                isFullscreen = false
            ) {
                if (isLoggedIn) {
                    VideoScreen(navController = navController)
                } else {
                    destinationAfterLogin = AppRoutes.VIDEOS
                    postLoginAction = "farm_videos"
                    LaunchedEffect(Unit) { navController.navigate(AppRoutes.AUTH) }
                }
            }
        }
        composable(
            route = "video_detail/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isFullscreen = isVideoFullscreen
            ) {
                VideoDetailScreen(
                    videoId = videoId,
                    navController = navController,
                    isFullscreen = isVideoFullscreen,
                    setFullscreen = { isVideoFullscreen = it }
                )
            }
        }
        composable(AppRoutes.CHAT) {
            AppScaffold(
                navController = navController,
                currentRoute = currentRoute,
                isFullscreen = false
            ) {
                if (isLoggedIn) {
                    val feedViewModel: FeedViewModel = viewModel()
                    val messageViewModel: MessageViewModel = viewModel()
                    val userPhone = AuthManager.getCurrentUserPhoneNumber(context) ?: ""

                    ChatScreen(
                        feedViewModel = feedViewModel,
                        messageViewModel = messageViewModel
                    )
                } else {
                    destinationAfterLogin = AppRoutes.CHAT
                    LaunchedEffect(Unit) { navController.navigate(AppRoutes.AUTH) }
                }
            }
        }
    }
}

@Composable
private fun AppScaffold(
    navController: NavHostController,
    currentRoute: String,
    isFullscreen: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0), // disables default bottom padding
        topBar = {
            if (!isFullscreen) {
                AppHeader(
                    title = "FarmHub",
                    onTitleClick = { navController.navigate(AppRoutes.INTRO) }
                )
            }
        },
        bottomBar = {
            if (!isFullscreen) {
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
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}