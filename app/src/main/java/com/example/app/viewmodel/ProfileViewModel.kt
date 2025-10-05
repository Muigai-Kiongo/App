package com.example.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.app.models.profile.UserProfileResponse
import com.example.app.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    var profile by mutableStateOf<UserProfileResponse?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    private val repository = ProfileRepository()

    // Only fetch if profile is not loaded yet
    fun fetchProfileIfNeeded() {
        if (profile == null && !isLoading) {
            fetchProfile()
        }
    }

    private fun fetchProfile() {
        isLoading = true
        error = null
        repository.getProfile(
            onResult = {
                isLoading = false
                profile = it
            },
            onError = {
                isLoading = false
                error = it
            }
        )
    }

    fun clearState() {
        profile = null
        error = null
        isLoading = false
    }
}