package com.example.app.features

sealed class HomeTab(val route: String) {
    object Intro : HomeTab("intro")
    object Help : HomeTab("help")
    object Video : HomeTab("video")
    object VideoDetail : HomeTab("videoDetail/{videoId}") {
        fun createRoute(videoId: Int) = "videoDetail/$videoId"
    }
    object Profile : HomeTab("profile")
    object Chat : HomeTab("chat")
    object Auth : HomeTab("auth")
}

