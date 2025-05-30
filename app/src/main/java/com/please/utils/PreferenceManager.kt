package com.please.utils

import android.content.Context
import android.content.SharedPreferences
import com.please.data.repositories.AuthRepository

/**
 * SharedPreferences를 사용한 데이터 저장 및 관리 클래스
 */
class PreferenceManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * 토큰을 가져오는 함수
     * 
     * @return 저장된 토큰 또는 메모리에 캐시된 토큰, 없는 경우 null
     */
    fun getToken(): String? {
        // 먼저 메모리에 캐시된 토큰을 확인
        val cachedToken = AuthRepository.AppState.userToken
        if (!cachedToken.isNullOrEmpty()) {
            return cachedToken
        }
        
        // SharedPreferences에서 토큰 조회
        val savedToken = sharedPreferences.getString(KEY_TOKEN, null)
        
        // 토큰이 SharedPreferences에만 있다면 메모리에도 캐시
        if (!savedToken.isNullOrEmpty() && cachedToken.isNullOrEmpty()) {
            AuthRepository.AppState.userToken = savedToken
        }
        
        return savedToken
    }
    
    /**
     * 토큰을 저장하는 함수
     * 
     * @param token 저장할 토큰
     */
    fun saveToken(token: String) {
        // 메모리 캐시 업데이트
        AuthRepository.AppState.userToken = token
        
        // SharedPreferences에 저장
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }
    
    /**
     * 토큰을 삭제하는 함수 (로그아웃 시 사용)
     */
    fun clearToken() {
        // 메모리 캐시 삭제
        AuthRepository.AppState.userToken = null
        
        // SharedPreferences에서 삭제
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
    
    /**
     * 사용자 ID를 저장하는 함수 (아이디 기억하기 기능용)
     * 
     * @param userId 저장할 사용자 ID
     */
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }
    
    /**
     * 저장된 사용자 ID를 가져오는 함수
     * 
     * @return 저장된 사용자 ID, 없으면 빈 문자열
     */
    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }
    
    companion object {
        private const val PREFERENCE_NAME = "Subtrack_Prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }
}
