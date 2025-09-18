package com.example.app.models.message

data class GetThreadsResponse(
    val status: String,
    val threads: List<ThreadItem>
)

data class ThreadItem(
    val senderId: String,
    val image: String,
    val recipientId: String,
    val read: Boolean,
    val text: String,
    val updatedAt: String,
    val recipient: UserName,
    val sender: UserName
)

data class UserName(
    val names: String
)