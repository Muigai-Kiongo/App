package com.example.app.models.signup

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val county: String,
    val password: String
)