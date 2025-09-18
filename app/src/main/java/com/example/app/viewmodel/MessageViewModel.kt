package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.repository.FeedRepository
import com.example.app.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MessageViewModel(
    private val messageRepository: MessageRepository = MessageRepository(),
    private val feedRepository: FeedRepository = FeedRepository()
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<com.example.app.models.feed.FeedItem>>(emptyList())
    val feedItems: StateFlow<List<com.example.app.models.feed.FeedItem>> get() = _feedItems

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> get() = _sending


    fun clearError() {
        _error.value = null
    }

    fun fetchFeed() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _feedItems.value = feedRepository.getUnifiedFeed()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createPost(text: String, imageUri: Uri, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _sending.value = true
            _error.value = null
            try {
                val result = messageRepository.createPost(text, imageUri, context)
                onResult(result)
                if (!result) _error.value = "Failed to send post"
            } catch (e: Exception) {
                _error.value = e.message
                onResult(false)
            } finally {
                _sending.value = false
            }
        }
    }

    fun sendMessage(text: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _sending.value = true
            _error.value = null
            try {
                val result = messageRepository.sendMessage(text)
                onResult(result)
                if (!result) _error.value = "Failed to send message"
            } catch (e: Exception) {
                _error.value = e.message
                onResult(false)
            } finally {
                _sending.value = false
            }
        }
    }
}