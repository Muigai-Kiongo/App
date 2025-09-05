package com.example.app.models.signup

data class RegisterResponse(
    val user: RegisteredUser,
    val success: Boolean? = null,
    val message: String? = null
)

data class RegisteredUser(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val names: String,
    val email: String?,
    val role: String,
    val phone: String,
    val county: String?,
    val subCounty: String?
)