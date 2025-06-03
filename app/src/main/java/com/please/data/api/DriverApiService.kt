package com.please.data.api

import com.please.data.models.driver.*
import retrofit2.Response
import retrofit2.http.*

interface DriverApiService {

    /**
     * 배송기사 홈 화면 정보를 가져오는 API
     *
     * @param authorization Bearer 토큰
     * @return 배송기사 홈 화면에 필요한 정보 (담당 위치, 수행 건수, 포인트 등)
     */
    @GET("driver/home")
    suspend fun getDriverHome(@Header("Authorization") authorization: String): Response<DriverHomeResponse>

    @GET("driver/pick-up")
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

    @GET("driver/delivery")
    suspend fun getDeliveryList(@Header("Authorization") authorization: String): Response<DeliveryResponse>

    @PATCH("driver/delivery/completed")
    suspend fun patchDeliveryComplete(@Header("Authorization") authorization: String, @Body id: TrackCodeRequest): Response<DeliveryComResponse>

    // ======= TSP 최적화 API =======

    /**
     * 다음 최적 목적지 가져오기 (TSP)
     */
    @GET("api/pickup/next")
    suspend fun getNextDestination(@Header("Authorization") authorization: String): Response<NextDestinationResponse>

    /**
     * 수거 완료 처리 (TSP)
     */
    @POST("api/pickup/complete")
    suspend fun completePickup(
        @Header("Authorization") authorization: String,
        @Body request: CompletePickupRequest
    ): Response<ApiResponse>

    /**
     * 허브 도착 완료 처리 (TSP)
     */
    @POST("api/pickup/hub-arrived")
    suspend fun completeHubArrival(@Header("Authorization") authorization: String): Response<ApiResponse>

    /**
     * 전체 수거 완료 상태 체크 (3시 이후 10분마다)
     */
    @GET("api/pickup/all-completed")
    suspend fun checkAllCompleted(): Response<PickAllResponse>
}