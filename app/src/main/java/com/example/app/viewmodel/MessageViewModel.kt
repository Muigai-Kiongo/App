package com.example.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.models.message.SendMessageResponse
import com.example.app.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SendMessageUiState {
    data object Idle : SendMessageUiState()
    data object Loading : SendMessageUiState()
    data class Success(val response: SendMessageResponse) : SendMessageUiState()
    data class Error(val message: String) : SendMessageUiState()
}

class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SendMessageUiState>(SendMessageUiState.Idle)
    val uiState: StateFlow<SendMessageUiState> = _uiState

    fun sendMessage(message: String, attachmentUri: Uri?) {
        _uiState.value = SendMessageUiState.Loading
        viewModelScope.launch {
            try {
                val response = messageRepository.sendMessage(message, attachmentUri)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = SendMessageUiState.Success(response.body()!!)
                } else {
                    _uiState.value = SendMessageUiState.Error("Failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = SendMessageUiState.Error("Exception: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = SendMessageUiState.Idle
    }
}