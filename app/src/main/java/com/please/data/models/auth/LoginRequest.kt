package com.please.data.models.auth

data class LoginRequest(
    val id: String,
    val password: String,
    val userType: UserType
)
