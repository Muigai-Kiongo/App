package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.models.feed.FeedItem
import com.example.app.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FeedRepository = FeedRepository()
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems: StateFlow<List<FeedItem>> get() = _feedItems

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Internal flag to avoid redundant fetching
    private var hasLoaded = false

    /**
     * Load feed only if not already loaded (e.g. screen start).
     */
    fun loadFeedIfNeeded() {
        if (!hasLoaded) {
            fetchFeed()
        }
    }

    /**
     * Explicitly fetch the feed (e.g. after sending a post, or pull-to-refresh).
     */
    fun fetchFeed() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _feedItems.value = repository.getUnifiedFeed()
                hasLoaded = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Force a new fetch from the repository (for manual refresh).
     */
    fun refreshFeed() {
        hasLoaded = false
        fetchFeed()
    }
}