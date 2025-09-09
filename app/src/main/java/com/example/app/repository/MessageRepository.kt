package com.example.app.repository

import android.content.Context
import android.net.Uri
import com.example.app.api.ApiClient
import com.example.app.models.message.SendMessageResponse
import com.example.app.session.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class MessageRepository(private val context: Context) {
    suspend fun sendMessage(
        message: String,
        attachmentUri: Uri?
    ): Response<SendMessageResponse> = withContext(Dispatchers.IO) {
        val phone = UserSession.phone ?: throw IllegalStateException("User phone not set")
        val messageBody = message.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())

        val attachmentPart = attachmentUri?.let { uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: ByteArray(0)
            inputStream?.close()
            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, bytes.size)
            MultipartBody.Part.createFormData(
                "attachment",
                "image.jpg",
                requestFile
            )
        }

        ApiClient.userService.sendMessageWithAttachment(
            messageBody,
            phoneBody,
            attachmentPart
        ).execute()
    }
}