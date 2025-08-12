package com.example.app.features

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.routes.AboutScreen
import com.example.app.routes.AuthScreen
import com.example.app.routes.HomeScreen
import com.example.app.routes.IntroScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable("intro") {
            IntroScreen(
                onGetStartedClick = { navController.navigate("home") },
                onLearnMoreClick = { navController.navigate("about") },
                onSignupClick = { navController.navigate("auth") }
            )
        }
        composable("about") {
            AboutScreen()
        }
        composable("home") {
            HomeScreen(
                onSearchClick = { navController.navigate("") }
            )
        }


        composable("auth") {
            AuthScreen(
                onLoginSuccess ={navController.navigate("home")}
                )
        }
    }
}
