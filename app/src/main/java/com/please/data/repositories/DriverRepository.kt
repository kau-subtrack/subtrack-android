package com.please.data.repositories

import com.please.data.api.DriverApiService
import com.please.data.models.driver.DeliveryComResponse
import com.please.data.models.driver.DeliveryResponse
import com.please.data.models.driver.DriverHomeResponse
import com.please.data.models.driver.IdRequest
import com.please.data.models.driver.PickComResponse
import com.please.data.models.driver.PickResponse
import com.please.data.models.driver.TrackCodeRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverRepository @Inject constructor(
    private val driverApiService: DriverApiService
) {
    
    /**
     * 배송기사 홈 화면 정보를 가져오는 함수
     * 
     * @param token 인증 토큰
     * @return 배송기사 홈 화면 데이터
     */
    suspend fun getDriverHome(token: String): Response<DriverHomeResponse> {
        return driverApiService.getDriverHome("Bearer $token")
    }

    suspend fun getPickupList(token: String): Response<PickResponse>{
        return driverApiService.getPickupList("Bearer $token")
    }

    suspend fun patchPickComplete(token: String, id: String): Response<PickComResponse>{
        val parcelId = IdRequest( ownerId = id )
        return driverApiService.patchPickComplete("Bearer $token", parcelId)
    }

    suspend fun getDeliveryList(token: String): Response<DeliveryResponse>{
        return driverApiService.getDeliveryList("Bearer $token")
    }

    suspend fun patchDeliveryComplete(token: String, track: String): Response<DeliveryComResponse>{
        val trackCode = TrackCodeRequest( trackingCode = track )
        return driverApiService.patchDeliveryComplete("Bearer $token", trackCode)
    }

}
