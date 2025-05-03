package com.please.data.models.auth

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val userType: UserType,
    val email: String?
)
