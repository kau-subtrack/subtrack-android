package com.please.data.models

data class SubscriptionPlan(
    val id: Int,
    val name: String,
    val price: Int,
    val points: Int,
    var isSelected: Boolean = false
)
