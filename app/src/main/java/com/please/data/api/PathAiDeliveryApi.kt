package com.please.data.api

import com.please.data.models.driver.DeliveryComRequest
import com.please.data.models.driver.DeliveryComResponse
import com.please.data.models.driver.HealthResponse
import com.please.data.models.driver.PathAiNextResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PathAiDeliveryApi {

    @GET("delivery/next")
    suspend fun getDeliveryNext(@Header("authorization") authorization: String): Response<PathAiNextResponse>

    @POST("delivery/complete")
    suspend fun postDeliveryComplete(@Header("authorization") authorization: String, @Body id: DeliveryComRequest): Response<DeliveryComResponse>

    @GET("delivery/status")
    suspend fun getDelStatus(): Response<HealthResponse>

}