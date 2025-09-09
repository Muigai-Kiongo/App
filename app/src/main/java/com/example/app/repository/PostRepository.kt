package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.posts.CreatePostResponse
import com.example.app.models.posts.GetAllPostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository {

    fun createPost(
        image: MultipartBody.Part,
        description: RequestBody,
        onResult: (CreatePostResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.userService.createPost(image, description)
            .enqueue(object : Callback<CreatePostResponse> {
                override fun onResponse(
                    call: Call<CreatePostResponse>,
                    response: Response<CreatePostResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        onError("Failed to create post: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                    onError("Network error: ${t.localizedMessage}")
                }
            })
    }

    fun getAllPosts(
        onResult: (GetAllPostsResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.userService.getAllPosts()
            .enqueue(object : Callback<GetAllPostsResponse> {
                override fun onResponse(
                    call: Call<GetAllPostsResponse>,
                    response: Response<GetAllPostsResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        onError("Failed to fetch posts: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetAllPostsResponse>, t: Throwable) {
                    onError("Network error: ${t.localizedMessage}")
                }
            })
    }
}