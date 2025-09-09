package com.example.app.models.message

data class SendMessageRequest(
    val message: String,
    val phone: String
)