package com.example.app.api

import com.example.app.models.login.LoginRequest
import com.example.app.models.login.LoginResponse
import com.example.app.models.message.SendMessageResponse
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

    @GET("posts")
    fun getAllPosts(): Call<GetAllPostsResponse>

    @Multipart
    @POST("posts")
    fun createPost(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<CreatePostResponse>


    @Multipart
    @POST("messaging")
    fun sendMessageWithAttachment(
        @Part("text") text: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part attachment: MultipartBody.Part?
    ): Call<SendMessageResponse>



//    @POST("auth/reset-password")
//    fun resetPass(@Body forgetPassRequest: ForgetPassRequest): Call<ForgetPassResponse>
//

//
//    @GET("messaging")
//    fun getThreads(): Call<MessageResponse>
//
//    @GET("media")
//    fun fetchVideos(): Call<VideoResponse>
}