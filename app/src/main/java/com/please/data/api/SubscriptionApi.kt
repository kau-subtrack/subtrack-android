package com.please.data.api

import com.please.data.models.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscriptionApi {
    @POST("subscription/subscribe")
    suspend fun subscribeToPlan(@Body request: Map<String, Int>): Response<SubscriptionResponse>

    @POST("subscription/purchase-points")
    suspend fun purchasePoints(): Response<SubscriptionResponse>
}
