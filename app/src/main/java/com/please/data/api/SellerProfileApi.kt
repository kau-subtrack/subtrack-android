package com.please.data.api

import com.please.data.models.AddressUpdateRequest
import com.please.data.models.ApiResponse
import com.please.data.models.PasswordUpdateRequest
import com.please.data.models.SellerProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface SellerProfileApi {
    
    @GET("seller/profile")
    suspend fun getSellerProfile(): Response<ApiResponse<SellerProfile>>
    
    @PUT("seller/address")
    suspend fun updateAddress(@Body request: AddressUpdateRequest): Response<ApiResponse<Unit>>
    
    @PUT("seller/password")
    suspend fun updatePassword(@Body request: PasswordUpdateRequest): Response<ApiResponse<Unit>>
}
