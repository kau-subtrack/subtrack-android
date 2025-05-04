package com.please.data.repositories

import com.please.data.api.SellerProfileApi
import com.please.data.models.AddressUpdateRequest
import com.please.data.models.ApiResponse
import com.please.data.models.PasswordUpdateRequest
import com.please.data.models.SellerProfile
import retrofit2.Response
import javax.inject.Inject

class SellerProfileRepository @Inject constructor(
    private val sellerProfileApi: SellerProfileApi
) {
    suspend fun getSellerProfile(): Result<SellerProfile> {
        return try {
            handleApiResponse(
                sellerProfileApi.getSellerProfile(),
                errorMessage = "프로필 조회에 실패했습니다.",
                nullDataMessage = "프로필 정보가 없습니다."
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAddress(
        address: String,
        detailAddress: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        return try {
            val request = AddressUpdateRequest(address, detailAddress, latitude, longitude)
            handleApiResponse(
                sellerProfileApi.updateAddress(request),
                errorMessage = "주소 업데이트에 실패했습니다."
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val request = PasswordUpdateRequest(currentPassword, newPassword)
            handleApiResponse(
                sellerProfileApi.updatePassword(request),
                errorMessage = "비밀번호 변경에 실패했습니다."
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 헬퍼 메서드 추가 - API 응답 처리 중복 제거
    private fun <T> handleApiResponse(
        response: Response<ApiResponse<T>>,
        errorMessage: String,
        nullDataMessage: String = "데이터가 없습니다."
    ): Result<T> {
        return if (response.isSuccessful && response.body() != null && response.body()!!.success) {
            response.body()!!.data?.let {
                Result.success(it)
            } ?: Result.failure(Exception(nullDataMessage))
        } else {
            Result.failure(Exception(response.body()?.message ?: errorMessage))
        }
    }
}
