package com.please.data.models.driver

/**
 * 수거 완료 API 응답 모델
 */
data class PickCompletedResponse(
    val status: Boolean,
    val data: PickCompletedData?,
    val message: String? = null
)

/**
 * 수거 완료 데이터 모델
 */
data class PickCompletedData(
    val address: String,
    val detailAddress: String,
    val pickupTimeWindow: String,
    val productName: String,
    val parcelCount: Int,
    val status: String
)
