package com.example.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.app.models.login.LoginResponse
import com.example.app.repository.LoginRepository

class LoginViewModel : ViewModel() {
    var loginResult by mutableStateOf<LoginResponse?>(null)
        private set
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
        private set

    private val repository = LoginRepository()

    fun login(phone: String, password: String) {
        loginError = null
        isLoading = true
        repository.login(phone, password,
            onResult = {
                isLoading = false
                loginResult = it
            },
            onError = {
                isLoading = false
                loginError = it
            }
        )
    }

    fun clearState() {
        loginError = null
        loginResult = null
        isLoading = false
    }
}