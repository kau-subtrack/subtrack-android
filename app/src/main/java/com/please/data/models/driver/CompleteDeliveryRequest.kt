package com.please.data.models.driver

/**
 * 배달 완료 요청 모델 - 서버 요구사항에 맞춤
 */
data class CompleteDeliveryRequest(
    val deliveryId: String  // 🔧 서버에서 deliveryId를 요구함
)

/**
 * 배달 완료 API 응답 모델
 */
data class CompleteDeliveryResponse(
    val status: String,
    val message: String? = null
)

/**
 * 배달 허브 도착 완료 응답 모델
 */
data class DeliveryHubArrivalResponse(
    val status: String,
    val message: String? = null,
    val location: HubLocation? = null,
    val arrivalTime: String? = null
)

data class HubLocation(
    val lat: Double,
    val lon: Double,
    val name: String
)
