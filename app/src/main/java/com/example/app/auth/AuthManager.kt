package com.example.app.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.*
import androidx.core.content.edit
import com.example.app.api.ApiClient
import com.example.app.session.UserSession

object AuthManager {
    private const val PREFS = "auth_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_COUNTY = "user_county"
    private const val KEY_USER_SUBCOUNTY = "user_subcounty"
    private const val KEY_USER_PAID = "user_paid"
    private const val KEY_ISSUED = "issued"
    private const val KEY_EXPIRES = "expires"
    private const val TOKEN_TIMEOUT_MILLIS = 12 * 60 * 60 * 1000L // 12 hours

    private val authState = MutableStateFlow(false)
    private var timeoutJob: Job? = null

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_TOKEN, null)
        val expires = prefs.getLong(KEY_EXPIRES, 0L)
        val now = System.currentTimeMillis()
        if (token != null && expires > now) {
            // Restore UserSession
            loadSession(context)
            // Set bearer token for ApiClient if valid
            ApiClient.setBearerToken(token)
            return true
        }
        // If expired, logout (clear token from both places)
        if (token != null) {
            logout(context)
        }
        return false
    }

    fun saveSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val expires = UserSession.expires ?: (now + TOKEN_TIMEOUT_MILLIS)
        prefs.edit {
            putString(KEY_TOKEN, UserSession.token)
            putLong(KEY_TOKEN_TIMESTAMP, now)
            putString(KEY_USER_ID, UserSession.userId)
            putString(KEY_USER_NAME, UserSession.userName)
            putString(KEY_USER_PHONE, UserSession.phone)
            putString(KEY_USER_ROLE, UserSession.role)
            putString(KEY_USER_COUNTY, UserSession.county)
            putString(KEY_USER_SUBCOUNTY, UserSession.subCounty)
            putString(KEY_USER_PAID, UserSession.paidUser)
            putLong(KEY_ISSUED, UserSession.issued ?: now)
            putLong(KEY_EXPIRES, expires)
        }
        authState.value = true
        ApiClient.setBearerToken(UserSession.token ?: "")
        startTimeout(context)
    }

    fun loadSession(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val expires = prefs.getLong(KEY_EXPIRES, 0L)
        val now = System.currentTimeMillis()
        if (expires > now) {
            UserSession.token = prefs.getString(KEY_TOKEN, null)
            UserSession.userId = prefs.getString(KEY_USER_ID, null)
            UserSession.userName = prefs.getString(KEY_USER_NAME, null)
            UserSession.phone = prefs.getString(KEY_USER_PHONE, null)
            UserSession.role = prefs.getString(KEY_USER_ROLE, null)
            UserSession.county = prefs.getString(KEY_USER_COUNTY, null)
            UserSession.subCounty = prefs.getString(KEY_USER_SUBCOUNTY, null)
            UserSession.paidUser = prefs.getString(KEY_USER_PAID, null)
            UserSession.issued = prefs.getLong(KEY_ISSUED, 0L)
            UserSession.expires = expires
            ApiClient.setBearerToken(UserSession.token ?: "")
            authState.value = true
            startTimeout(context)
            return true
        }
        logout(context)
        return false
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit {
            clear()
        }
        UserSession.clear()
        authState.value = false
        ApiClient.clearBearerToken()
        timeoutJob?.cancel()
    }

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_TOKEN, token)
            putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
            putLong(KEY_EXPIRES, System.currentTimeMillis() + TOKEN_TIMEOUT_MILLIS)
        }
        UserSession.token = token
        UserSession.issued = System.currentTimeMillis()
        UserSession.expires = System.currentTimeMillis() + TOKEN_TIMEOUT_MILLIS
        authState.value = true
        ApiClient.setBearerToken(token)
        startTimeout(context)
    }

    fun getCurrentUserPhoneNumber(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_PHONE, null)
    }

    fun addAuthStateListener(context: Context, listener: (Boolean) -> Unit) {
        listener(isLoggedIn(context))
    }

    private fun startTimeout(context: Context) {
        timeoutJob?.cancel()
        timeoutJob = CoroutineScope(Dispatchers.Default).launch {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val expires = prefs.getLong(KEY_EXPIRES, 0L)
            val now = System.currentTimeMillis()
            val remaining = expires - now
            if (remaining > 0) {
                delay(remaining)
            }
            logout(context)
        }
    }
}