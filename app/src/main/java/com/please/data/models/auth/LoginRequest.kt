package com.please.data.models.auth

data class LoginRequest(
    val email: String,
    val password: String,
    val userType: UserType
)
