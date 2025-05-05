package com.example.subtrack.data.repository

import android.util.Log
import com.example.subtrack.data.model.HomeResponse
import com.example.subtrack.service.ApiClient
import com.example.subtrack.util.TokenManager

class HomeRepository(private val tokenManager: TokenManager) {
    
    private val apiService = ApiClient.apiService
    
    suspend fun getHomeInfo(): Result<HomeResponse> {
        return try {
            val formattedToken = tokenManager.getFormattedToken()
            Log.d("HomeRepository", "Token: $formattedToken")
            
            val response = apiService.getHomeInfo(formattedToken)
            Log.d("HomeRepository", "Response code: ${response.code()}")
            Log.d("HomeRepository", "Response body: ${response.body()}")
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("HomeRepository", "Error body: $errorBody")
                Result.failure(Exception(errorBody ?: "Unknown error occurred"))
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
