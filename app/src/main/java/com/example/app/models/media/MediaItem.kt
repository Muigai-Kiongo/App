package com.example.app.models.media


data class MediaItem(
    val id: String,
    val title: String,
    val company: String,
    val description: String,
    val video: String,
    val thumbnail: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String
)