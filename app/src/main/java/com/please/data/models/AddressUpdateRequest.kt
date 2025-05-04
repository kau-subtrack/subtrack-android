package com.please.data.models

data class AddressUpdateRequest(
    val address: String,
    val detailAddress: String,
    val latitude: Double,
    val longitude: Double
)
