package com.please.data.models.driver

/**
 * AI가 제안한 최적 경로 응답
 */
data class PathAiDeliveryResponse(
    val routes: List<Route>,
    val message: String,
    val success: Boolean
)

/**
 * 경로 정보
 */
data class Route(
    val steps: List<Step>,
    val distance: Double,
    val duration: Int
)

/**
 * 경로 단계별 정보
 */
data class Step(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val orderName: String,
    val stepType: String // PICKUP, DELIVERY 등
)
