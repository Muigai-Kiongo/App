package com.example.app.models.feed

import com.example.app.models.posts.Post
import com.example.app.models.message.ThreadItem

sealed class FeedItem(open val time: String) {
    data class PostItem(val post: Post) : FeedItem(post.createdAt)
    data class ThreadItemFeed(val thread: ThreadItem) : FeedItem(thread.updatedAt)
}

