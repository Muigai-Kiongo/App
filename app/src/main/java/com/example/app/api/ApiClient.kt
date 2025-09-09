package com.example.app.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://3a8099357212.ngrok-free.app"
    @Volatile private var bearerToken: String? = null

    fun setBearerToken(token: String?) {
        bearerToken = token
    }

    fun clearBearerToken() {
        bearerToken = null
    }

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val builder = original.newBuilder()
        bearerToken?.let {
            builder.header("Authorization", "Bearer $it")
        }
        chain.proceed(builder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}