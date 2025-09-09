package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.message.SendMessageRequest
import com.example.app.models.message.SendMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class MessageRepository {
    suspend fun sendMessage(text: String): Response<SendMessageResponse> = withContext(Dispatchers.IO) {
        val request = SendMessageRequest(message = text)
        ApiClient.userService.sendMessage(request).execute()
    }
}