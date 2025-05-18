package com.please.data.api

import com.please.data.models.seller.PlanId
import com.please.data.models.seller.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SubscriptionApi {

    //points
    @POST("owner/points/subscribe")
    suspend fun subscribe(@Header("authorization") authorization: String, @Body id: PlanId): Response<SubscriptionResponse> //Response<ApiResponse<SellerHomeInfo>>

    @POST("subscription/purchase-points")
    suspend fun purchasePoints(): Response<SubscriptionResponse>

    /* - 프론트 UI 미존재. 따라서 미구현.
    @POST("owner/points/charge")
    suspend fun charge(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>
    @GET("owner/points/history")
    suspend fun history(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>
     */
}
