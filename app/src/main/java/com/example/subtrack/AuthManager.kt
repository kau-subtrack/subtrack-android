package com.example.subtrack

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun isLoggedIn(context: Context): Boolean {
        // TODO: 개발 중에는 항상 true를 반환하여 로그인을 건너뜀
        // 완성본에서는 이 부분을 제거하고 실제 로그인 체크 로직으로 대체
        return true  // 개발용 - 항상 로그인된 것으로 처리
        
        // 원래 로직 (완성본에서 사용)
        // return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
}
