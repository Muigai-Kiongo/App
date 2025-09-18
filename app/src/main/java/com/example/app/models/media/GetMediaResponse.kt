package com.example.app.models.media

data class GetMediaResponse(
    val media: List<MediaItem>,
    val status: String
)