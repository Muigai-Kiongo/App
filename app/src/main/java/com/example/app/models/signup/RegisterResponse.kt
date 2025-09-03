package com.example.app.models.signup

data class RegisterResponse(
    val success: Boolean,
    val message: String
    // Add more fields if your backend returns them, e.g. userId, token, etc.
)