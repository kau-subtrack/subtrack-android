package com.please.data.api

import com.please.data.api.PathAiPickupApi.AppUser.userId
import com.please.data.models.driver.DeliveryComRequest
import com.please.data.models.driver.DeliveryComResponse
import com.please.data.models.driver.HealthResponse
import com.please.data.models.driver.ParceIdRequest
import com.please.data.models.driver.PathAiNextResponse
import com.please.data.models.driver.PickAllResponse
import com.please.data.models.driver.WebhookResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PathAiPickupApi {
    object AppUser {
        var userId: String = "0"
    }

    @POST("pickup/webhook")
    suspend fun postWebhook(@Body id: ParceIdRequest): Response<WebhookResponse>

    //@POST(value = "pickup/next/$userId") // 4 is num -> val. how..? - 흠... 안되는데? 그렇다고 1~5까지 그럴수는없고.
    //suspend fun postPickNext(@Body id: ParceIdRequest): Response<PathAiNextResponse>

    @POST("pickup/complete")
    suspend fun postPickCom(@Body id: ParceIdRequest): Response<HealthResponse>

    @GET("pickup/all-completed")
    suspend fun postPickAll(): Response<PickAllResponse>

    @GET("pickup/status")
    suspend fun getPickStatus(): Response<HealthResponse>
}