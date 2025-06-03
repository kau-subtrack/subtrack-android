package com.please.data.models.driver

data class CompletePickupRequest(
    val parcelId: String
)

data class ApiResponse(
    val status: String,
    val message: String? = null
)

