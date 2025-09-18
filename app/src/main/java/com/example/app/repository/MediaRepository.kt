package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.media.GetMediaResponse
import retrofit2.Response

class MediaRepository {
    suspend fun fetchMedia(): Response<GetMediaResponse> {
        return ApiClient.userService.getMedia()
    }
}