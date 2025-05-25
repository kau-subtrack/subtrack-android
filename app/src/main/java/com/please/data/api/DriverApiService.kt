package com.please.data.api

import com.please.data.models.driver.DriverHomeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DriverApiService {
    
    /**
     * 배송기사 홈 화면 정보를 가져오는 API
     * 
     * @param authorization Bearer 토큰
     * @return 배송기사 홈 화면에 필요한 정보 (담당 위치, 수행 건수, 포인트 등)
     */
    @GET("driver/home")
    suspend fun getDriverHome(@Header("Authorization") authorization: String): Response<DriverHomeResponse>
}
