package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.models.media.MediaItem
import com.example.app.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MediaUiState {
    data object Loading : MediaUiState()
    data class Success(val videos: List<MediaItem>) : MediaUiState()
    data class Error(val message: String) : MediaUiState()
}

class MediaViewModel : ViewModel() {

    private val repository = MediaRepository()

    private val _uiState = MutableStateFlow<MediaUiState>(MediaUiState.Loading)
    val uiState: StateFlow<MediaUiState> = _uiState

    // --- Enhancement for playback buffer optimization ---
    // Playback state cache for quick resume (per videoId)
    private val _playbackPositions = mutableMapOf<String, Long>()
    fun getPlaybackPosition(videoId: String): Long = _playbackPositions[videoId] ?: 0L
    fun setPlaybackPosition(videoId: String, position: Long) {
        _playbackPositions[videoId] = position
    }

    // Track last played videoId for state restoration
    private var _lastPlayedVideoId: String? = null
    fun setLastPlayedVideoId(videoId: String) {
        _lastPlayedVideoId = videoId
    }
    // --- End enhancement ---

    init {
        loadMedia() // Automatically load media when ViewModel is created
    }

    fun loadMedia() {
        _uiState.value = MediaUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.fetchMedia()
                if (response.isSuccessful) {
                    val media = response.body()?.media ?: emptyList()
                    _uiState.value = MediaUiState.Success(media)
                } else {
                    _uiState.value = MediaUiState.Error("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = MediaUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}