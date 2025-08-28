package com.example.app.features

sealed class HomeTab(val route: String) {
    data object Intro : HomeTab("home/intro")
    data object Auth : HomeTab("home/auth")
    data object Help : HomeTab("home/help")
    data object Video : HomeTab("home/video")
    data object Profile : HomeTab("home/profile")
    data object Chat : HomeTab("home/chat")
}
