package com.example.app.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://127.0.0.1:8080/" // Replace with your API host if different

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}