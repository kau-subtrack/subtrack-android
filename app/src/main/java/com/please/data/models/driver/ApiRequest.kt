package com.please.data.models.driver

data class ApiResponse(
    val status: String,
    val message: String? = null
)

data class TspApiResponse(
    val status: String,
    val message: String? = null
)