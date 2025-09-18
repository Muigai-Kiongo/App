package com.example.app.session

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "user_session"
private const val SESSION_EXPIRY_MILLIS = 12 * 60 * 60 * 1000L // 12 hours

object UserSessionPersistence {

    fun saveSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("token", UserSession.token)
            putString("userId", UserSession.userId)
            putString("userName", UserSession.userName)
            putString("phone", UserSession.phone)
            putString("role", UserSession.role)
            putString("county", UserSession.county)
            putString("subCounty", UserSession.subCounty)
            putString("paidUser", UserSession.paidUser)
            putLong("issued", UserSession.issued ?: System.currentTimeMillis())
            // If your backend doesn't set expires, set it to now + 12h
            putLong("expires", UserSession.expires ?: (System.currentTimeMillis() + SESSION_EXPIRY_MILLIS))
            apply()
        }
    }

    fun loadSession(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val expires = prefs.getLong("expires", 0L)
        val now = System.currentTimeMillis()
        return if (expires > now) {
            UserSession.token = prefs.getString("token", null)
            UserSession.userId = prefs.getString("userId", null)
            UserSession.userName = prefs.getString("userName", null)
            UserSession.phone = prefs.getString("phone", null)
            UserSession.role = prefs.getString("role", null)
            UserSession.county = prefs.getString("county", null)
            UserSession.subCounty = prefs.getString("subCounty", null)
            UserSession.paidUser = prefs.getString("paidUser", null)
            UserSession.issued = prefs.getLong("issued", 0L)
            UserSession.expires = expires
            true
        } else {
            false // Session expired
        }
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { clear() }
        UserSession.clear()
    }
}