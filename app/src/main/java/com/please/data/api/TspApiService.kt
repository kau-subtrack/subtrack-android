package com.please.data.api

import com.please.data.models.driver.NextDestinationResponse
import com.please.data.models.driver.CompletePickupRequest
import com.please.data.models.driver.ApiResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * TSP (수거 최적화) API 서비스 인터페이스
 */
interface TspApiService {
    /**
     * 다음 목적지 조회 (TSP 최적화 결과)
     *
     * @param authorization Bearer 토큰
     * @return 다음 목적지 정보와 최적 경로
     */
    @GET("api/pickup/next")
    suspend fun getNextDestination(
        @Header("Authorization") authorization: String
    ): Response<NextDestinationResponse>

    /**
     * 수거 완료 처리
     *
     * @param authorization Bearer 토큰
     * @param request 완료할 소포 ID
     * @return 완료 처리 결과
     */
    @POST("api/pickup/complete")
    suspend fun completePickup(
        @Header("Authorization") authorization: String,
        @Body request: CompletePickupRequest
    ): Response<ApiResponse>

    /**
     * 허브 도착 완료 처리
     *
     * @param authorization Bearer 토큰
     * @return 허브 도착 처리 결과
     */
    @POST("api/pickup/hub-arrived")
    suspend fun hubArrived(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse>
}