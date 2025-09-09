package com.example.app.api

import com.example.app.models.login.LoginResponse
import com.example.app.models.login.LoginRequest
import com.example.app.models.posts.CreatePostResponse
import com.example.app.models.posts.GetAllPostsResponse
import com.example.app.models.profile.UserProfileResponse
import com.example.app.models.signup.RegisterRequest
import com.example.app.models.signup.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserService {


    @POST("auth/login")
    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("auth/me")
    fun getUserProfile(): Call<UserProfileResponse>


    @Multipart
    @POST("posts")
    fun createPost(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<CreatePostResponse>

    @GET("posts")
    fun getAllPosts(): Call<GetAllPostsResponse>


//    @GET("auth/me")
//    fun profileUser(): Call<ProfileResponse>
//
//    @POST("auth/reset-password")
//    fun resetPass(@Body forgetPassRequest: ForgetPassRequest): Call<ForgetPassResponse>
//
//    @Multipart
//    @POST("posts")
//    fun postQuestion(
//        @Part("description") description: RequestBody,
//        @Part image: MultipartBody.Part
//    ): Call<PostResponse>
//
//    @GET("messaging")
//    fun getThreads(): Call<MessageResponse>
//
//    @GET("media")
//    fun fetchVideos(): Call<VideoResponse>
}