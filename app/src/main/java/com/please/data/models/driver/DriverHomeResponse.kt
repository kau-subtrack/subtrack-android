package com.please.data.models.driver

import com.google.gson.annotations.SerializedName

// API 응답을 위한 데이터 클래스
data class DriverHomeResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: DriverHomeData
)

data class DriverHomeData(
    @SerializedName("region")
    val region: String = "",
    @SerializedName("monthlyCount")
    val monthlyCount: MonthlyCount,
    @SerializedName("todayCount")
    val todayCount: TodayCount,
    @SerializedName("points")
    val points: Int = 0
)

data class MonthlyCount(
    @SerializedName("pickup")
    val pickup: Int = 0,
    @SerializedName("delivery")
    val delivery: Int = 0,
    @SerializedName("total")
    val total: Int = 0
)

// API 응답에서 문자열로 올 수 있는 필드들을 위한 조정
data class TodayCount(
    @SerializedName("pickup")
    private val _pickup: Any = 0,
    @SerializedName("delivery")
    private val _delivery: Any = 0
) {
    // pickup 값을 안전하게 Int로 변환
    val pickup: Int
        get() = when (_pickup) {
            is Int -> _pickup
            is String -> _pickup.toIntOrNull() ?: 0
            else -> 0
        }
    
    // delivery 값을 안전하게 Int로 변환
    val delivery: Int
        get() = when (_delivery) {
            is Int -> _delivery
            is String -> _delivery.toIntOrNull() ?: 0
            else -> 0
        }
}
