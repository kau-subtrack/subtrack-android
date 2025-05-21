package com.please.data.repositories

import com.please.data.api.DriverApiService
import com.please.data.models.driver.DriverHomeResponse
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
}
