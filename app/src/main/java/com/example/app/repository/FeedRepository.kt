package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.feed.FeedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository {

    // Fetch posts from /posts endpoint
    suspend fun getAllPosts(): List<FeedItem.PostItem> = withContext(Dispatchers.IO) {
        val response = ApiClient.userService.getAllPosts().execute()
        if (response.isSuccessful && response.body() != null) {
            // If posts is a List<PostWrapper>
            response.body()!!.posts.map { FeedItem.PostItem(it.post) }
        } else {
            emptyList()
        }
    }

    // Fetch threads from /messaging endpoint
    suspend fun getAllThreads(): List<FeedItem.ThreadItemFeed> = withContext(Dispatchers.IO) {
        val response = ApiClient.userService.getThreads().execute()
        if (response.isSuccessful && response.body() != null) {
            response.body()!!.threads.map { FeedItem.ThreadItemFeed(it) }
        } else {
            emptyList()
        }
    }

    // Fetch and combine both, sorted by time descending
    suspend fun getUnifiedFeed(): List<FeedItem> = withContext(Dispatchers.IO) {
        val posts = getAllPosts()
        val threads = getAllThreads()
        (posts + threads).sortedByDescending { it.time }
    }
}