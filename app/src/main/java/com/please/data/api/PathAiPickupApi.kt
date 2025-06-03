package com.please.data.api

import com.please.data.models.driver.*
import retrofit2.Response
import retrofit2.http.*

interface PathAiPickupApi {

    @POST("pickup/webhook")
    suspend fun postWebhook(@Body id: ParceIdRequest): Response<WebhookResponse>

    // 🔧 수정: GET으로 변경하고 인증 헤더 추가
    @GET("pickup/next")
    suspend fun getPickupNext(@Header("Authorization") authorization: String): Response<NextDestinationResponse>

    // 🔧 수정: 완료 API 추가
    @POST("pickup/complete")
    suspend fun postPickupComplete(
        @Header("Authorization") authorization: String,
        @Body request: CompletePickupRequest
    ): Response<TspApiResponse>

    // 🔧 추가: 허브 도착 API
    @POST("pickup/hub-arrived")
    suspend fun postHubArrival(@Header("Authorization") authorization: String): Response<TspApiResponse>

    @GET("pickup/all-completed")
    suspend fun postPickAll(): Response<PickAllResponse>

    @GET("pickup/status")
    suspend fun getPickStatus(): Response<HealthResponse>
}