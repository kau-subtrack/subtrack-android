package com.please.data.models

data class SellerProfile(
    val id: String,
    val name: String,
    val address: String,
    val detailAddress: String,
    val latitude: Double,
    val longitude: Double
)
