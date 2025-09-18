package com.example.app.repository

import android.content.Context
import android.net.Uri
import com.example.app.api.ApiClient
import com.example.app.session.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MessageRepository {

    suspend fun createPost(text: String, imageUri: Uri, context: Context): Boolean = withContext(Dispatchers.IO) {
        val stream = context.contentResolver.openInputStream(imageUri)
        val bytes = stream?.readBytes()
        stream?.close()
        val requestBody = bytes?.let {
            it.toRequestBody("image/*".toMediaTypeOrNull(), 0, it.size)
        } ?: return@withContext false

        val imagePart = MultipartBody.Part.createFormData("image", "attachment.jpg", requestBody)
        val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = ApiClient.userService.createPost(imagePart, textPart).execute()
        response.isSuccessful
    }

    suspend fun sendMessage(text: String): Boolean = withContext(Dispatchers.IO) {
        val phone = UserSession.phone
        if (phone.isNullOrBlank()) return@withContext false // Optionally handle session error

        val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = ApiClient.userService.sendMessage(textPart, phonePart).execute()
        response.isSuccessful
    }
}