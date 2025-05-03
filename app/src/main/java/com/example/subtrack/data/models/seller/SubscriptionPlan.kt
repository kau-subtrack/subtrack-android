package com.please.data.models.seller

data class SubscriptionPlan(
    val id: Int,
    val name: String,
    val price: Int,
    val points: Int,
    val isSelected: Boolean = false
)
