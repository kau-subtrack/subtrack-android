package com.please.data.api

import com.please.data.models.auth.LoginRequest
import com.please.data.models.auth.LoginResponse
import com.please.data.models.auth.RegisterRequest
import com.please.data.models.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>
    
    @GET("auth/checkId")
    suspend fun checkIdDuplicate(@Query("id") id: String): Response<Boolean>
}
