package com.please.data.repositories

import com.please.data.api.PathAiDeliveryApi
import com.please.data.api.PathAiPickupApi
import com.please.data.models.driver.HealthResponse
import com.please.data.models.seller.DeliveryBaseResponse
import com.please.data.models.seller.SellerHomeInfo
import retrofit2.Response
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PathAiRespository @Inject constructor(
    private val PathPickApi: PathAiPickupApi,
    private val PathDeliveryApi: PathAiDeliveryApi
){

    suspend fun checkPickHealth(): Response<HealthResponse> {
        return PathPickApi.getPickStatus()
    }

    suspend fun checkDelHealth(): Response<HealthResponse> {
        return PathDeliveryApi.getDelStatus()
    }

    /*
    suspend fun ownerHome(token: String): Response<SellerHomeInfo> {
        return ownerService.ownerHome("Bearer $token")
    }

    suspend fun shipmentCompleted(token: String, date: Date): Response<DeliveryBaseResponse>{
        val calendar = Calendar.getInstance().apply { time = date }
        val params = mapOf(
            "year" to calendar.get(Calendar.YEAR).toString(),
            "month" to (calendar.get(Calendar.MONTH) + 1).toString(), //왠지는 모르겠지만 월만 -1임.
            "day" to calendar.get(Calendar.DATE).toString() // 안 쓰이는 파라미터.
        )

        return ownerService.shipmentCompleted("Bearer $token", params)
    }
    */

}