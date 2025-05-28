package com.please.data.models.driver

data class PathAiPickup(
    val i: Int
)

data class ParceIdRequest(
    val parcelId: String
)


data class WebhookResponse(
    val status: String,
    val parcelId: String,
    val district: String, // 수거 지역 구.
    val driverId: Int,   // 할당된 기사 ID (1-5)
    val coordinates: Coordinates
)

data class PickAllResponse(
    val completed: Boolean,
    val message: String?,
    val total_converted: Int?,
    val import_status: Int?,
    val assign_status: Int?,
    val error: String?
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)