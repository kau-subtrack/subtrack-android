package com.please.data.models

data class PasswordUpdateRequest(
    val currentPassword: String,
    val newPassword: String
)
