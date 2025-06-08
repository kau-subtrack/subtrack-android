package com.please.data.models.driver

/**
 * 수거 완료 요청 모델
 */
data class CompletePickupRequest(
    val parcelId: String  // 🔧 수거는 항상 parcelId 사용
)

/**
 * 수거 완료 API 응답 모델
 */
data class CompletePickupResponse(
    val status: String,
    val message: String? = null
)