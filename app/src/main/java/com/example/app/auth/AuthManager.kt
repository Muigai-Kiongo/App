package com.example.app.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.*
import androidx.core.content.edit
import com.example.app.api.ApiClient

object AuthManager {
    private const val PREFS = "auth_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    private const val TOKEN_TIMEOUT_MILLIS = 12 * 60 * 60 * 1000L // 12 hours

    private val authState = MutableStateFlow(false)
    private var timeoutJob: Job? = null

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_TOKEN, null)
        val timestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0L)
        val now = System.currentTimeMillis()
        if (token != null && now - timestamp < TOKEN_TIMEOUT_MILLIS) {
            // Set bearer token for ApiClient if valid
            ApiClient.setBearerToken(token)
            return true
        }
        // If expired, logout (clear token from both places)
        if (token != null && now - timestamp >= TOKEN_TIMEOUT_MILLIS) {
            logout(context)
        }
        return false
    }

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_TOKEN, token)
            putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
        }
        authState.value = true
        ApiClient.setBearerToken(token)
        startTimeout(context)
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_TOKEN)
            remove(KEY_TOKEN_TIMESTAMP)
        }
        authState.value = false
        ApiClient.clearBearerToken()
        timeoutJob?.cancel()
    }

    // Call whenever you want to observe changes
    fun addAuthStateListener(context: Context, listener: (Boolean) -> Unit) {
        listener(isLoggedIn(context))
    }

    private fun startTimeout(context: Context) {
        timeoutJob?.cancel()
        timeoutJob = CoroutineScope(Dispatchers.Default).launch {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val timestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0L)
            val now = System.currentTimeMillis()
            val remaining = TOKEN_TIMEOUT_MILLIS - (now - timestamp)
            if (remaining > 0) {
                delay(remaining)
            }
            logout(context)
        }
    }
}