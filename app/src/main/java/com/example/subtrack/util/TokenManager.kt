package com.example.subtrack.util

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        android.util.Log.d("TokenManager", "Saving token: $token")
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        android.util.Log.d("TokenManager", "Getting token: $token")
        return token
    }
    
    fun clearToken() {
        android.util.Log.d("TokenManager", "Clearing token")
        prefs.edit().remove(KEY_TOKEN).apply()
    }
    
    fun getFormattedToken(): String {
        val token = getToken()
        val formattedToken = "Bearer $token"
        android.util.Log.d("TokenManager", "Formatted token: $formattedToken")
        return formattedToken
    }
    
    companion object {
        private const val PREF_NAME = "SubtrackPrefs"
        private const val KEY_TOKEN = "access_token"
    }
}
