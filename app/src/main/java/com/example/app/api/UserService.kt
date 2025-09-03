package com.example.app.api

import com.example.app.models.login.LoginResponse
import com.example.app.models.login.LoginRequest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("auth/login")
    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>







//    @POST("auth/register")
//    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
//
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