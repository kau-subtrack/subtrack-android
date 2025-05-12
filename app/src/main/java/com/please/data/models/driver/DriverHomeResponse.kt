package com.please.data.models.driver

import com.google.gson.annotations.SerializedName

// API 응답을 위한 데이터 클래스 (추후 API 연동 시 사용)
data class DriverHomeResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: DriverHomeData
)

data class DriverHomeData(
    @SerializedName("region")
    val region: String,
    @SerializedName("monthlyCount")
    val monthlyCount: MonthlyCount,
    @SerializedName("todayCount")
    val todayCount: TodayCount,
    @SerializedName("points")
    val points: Int
)

data class MonthlyCount(
    @SerializedName("pickup")
    val pickup: Int,
    @SerializedName("delivery")
    val delivery: Int,
    @SerializedName("total")
    val total: Int
)

data class TodayCount(
    @SerializedName("pickup")
    val pickup: Int,
    @SerializedName("delivery")
    val delivery: Int
)
