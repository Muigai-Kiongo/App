package com.example.app.models.posts

data class GetAllPostsResponse(
    val status: String,
    val posts: List<PostWrapper>
)