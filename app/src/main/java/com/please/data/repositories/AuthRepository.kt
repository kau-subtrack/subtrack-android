package com.please.data.repositories

import com.please.data.api.AuthApiService
import com.please.data.models.auth.LoginRequest
import com.please.data.models.auth.LoginResponse
import com.please.data.models.auth.RegisterRequest
import com.please.data.models.auth.RegisterResponse
import com.please.data.models.auth.UserType
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {
    object AppState {
        var userToken: String? = null
    }

    suspend fun login(id: String, password: String, userType: UserType): Response<LoginResponse> {
        return apiService.login(LoginRequest(id, password, userType))
    }
    
    suspend fun register(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(registerRequest)
    }
    
    suspend fun checkIdDuplicate(id: String): Response<Boolean> {
        return apiService.checkIdDuplicate(id)
    }
    
    fun logout() {
        // 로그인 토큰 및 사용자 정보 삭제
        AppState.userToken = null
        // 추후 SharedPreferences나 다른 저장소에서 사용자 정보 삭제 로직 추가 가능
    }
}
