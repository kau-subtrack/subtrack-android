package com.please.data.api

import com.please.data.models.AddressUpdateRequest
import com.please.data.models.ApiResponse
import com.please.data.models.PasswordUpdateRequest
import com.please.data.models.SellerProfile
import com.please.data.models.seller.SellerHomeInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface SellerProfileApi {

    //home
    @GET("owner/home")
    suspend fun ownerHome(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    //shipment

    //points

    @GET("owner/profile")
    suspend fun getSellerProfile(): Response<ApiResponse<SellerProfile>>

    //미사용
    @PUT("owner/address")
    suspend fun updateAddress(@Body request: AddressUpdateRequest): Response<ApiResponse<Unit>>

    //미사용
    @PUT("owner/password")
    suspend fun updatePassword(@Body request: PasswordUpdateRequest): Response<ApiResponse<Unit>>
}
