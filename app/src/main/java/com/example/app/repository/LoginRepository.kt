package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.login.LoginRequest
import com.example.app.models.login.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    fun login(
        phone: String,
        password: String,
        onResult: (LoginResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = LoginRequest(phone, password)
        try {
            ApiClient.userService.userLogin(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        try {
                            if (response.isSuccessful && response.body() != null) {
                                // Store token for Bearer authentication
                                ApiClient.setBearerToken(response.body()!!.token)
                                onResult(response.body())
                            } else {
                                onError("Invalid credentials or server error.")
                            }
                        } catch (e: Exception) {
                            onError("Unexpected error: ${e.localizedMessage ?: "Something went wrong."}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        onError("Network error: ${t.localizedMessage ?: "Please check your connection and try again."}")
                    }
                })
        } catch (e: Exception) {
            onError("Unexpected error: ${e.localizedMessage ?: "Something went wrong."}")
        }
    }
}