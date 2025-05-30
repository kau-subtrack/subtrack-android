package com.please.data.api

import com.please.data.models.driver.DeliveryComResponse
import com.please.data.models.driver.DeliveryResponse
import com.please.data.models.driver.DriverHomeResponse
import com.please.data.models.driver.IdRequest
import com.please.data.models.driver.PathAiDeliveryResponse
import com.please.data.models.driver.PickCompletedResponse
import com.please.data.models.driver.PickResponse
import com.please.data.models.driver.TrackCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface DriverApiService {
    
    /**
     * 배송기사 홈 화면 정보를 가져오는 API
     * 
     * @param authorization Bearer 토큰
     * @return 배송기사 홈 화면에 필요한 정보 (담당 위치, 수행 건수, 포인트 등)
     */
    @GET("driver/home")
    suspend fun getDriverHome(@Header("Authorization") authorization: String): Response<DriverHomeResponse>

    @GET("/driver/pick-up")
    suspend fun getPickupList(@Header("Authorization") authorization: String): Response<PickResponse>

    /**
     * 수거 완료 처리 API
     * 
     * @param authorization Bearer 토큰
     * @param id 소유자 ID (ownerId)
     * @return 수거 완료 처리 응답
     */
    @PATCH("driver/pick-up/completed")
    suspend fun patchPickComplete(@Header("Authorization") authorization: String, @Body id: IdRequest): Response<PickCompletedResponse>

    @GET
    suspend fun getDeliveryList(@Header("Authorization") authorization: String): Response<DeliveryResponse>

    @PATCH
    suspend fun patchDeliveryComplete(@Header("Authorization") authorization: String, @Body id: TrackCodeRequest): Response<DeliveryComResponse>

}
