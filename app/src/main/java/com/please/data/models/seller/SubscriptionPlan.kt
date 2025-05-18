package com.please.data.models.seller

import java.util.Date

//미사용
data class SubscriptionPlan(
    val id: Int,
    val name: String,
    val price: Int,
    val points: Int,
    val isSelected: Boolean = false
)

//일단 양식 맞추기 위한 클래스 생성
data class PlanId(
    val planId: Int
)

//반환값이 있긴한데, 프론트에서 쓰이는지 모르겠음.
data class SubscriptionResponse(
    val status: Boolean,
    val message: String,
    val planName: String,
    val grantedPoints: Int,
    val currentPoints: Int,
    val subscriptionStartDate: Date
)