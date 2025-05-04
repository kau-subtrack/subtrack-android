package com.please.data.repositories

import com.please.data.api.AuthApiService
import com.please.data.models.auth.LoginRequest
import com.please.data.models.auth.LoginResponse
import com.please.data.models.auth.RegisterRequest
import com.please.data.models.auth.RegisterResponse
import com.please.data.models.auth.UserType
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {
    suspend fun login(id: String, password: String, userType: UserType): Response<LoginResponse> {
        return apiService.login(LoginRequest(id, password, userType))
    }
    
    suspend fun register(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(registerRequest)
    }
    
    suspend fun checkIdDuplicate(id: String): Response<Boolean> {
        return apiService.checkIdDuplicate(id)
    }
}
