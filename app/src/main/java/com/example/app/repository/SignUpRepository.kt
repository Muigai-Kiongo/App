package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.signup.RegisterRequest
import com.example.app.models.signup.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepository {
    fun signup(
        registerRequest: RegisterRequest,
        onResult: (RegisterResponse?) -> Unit,
        onError: (String) -> Unit
    ) {
        ApiClient.userService.registerUser(registerRequest)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(response.body())
                    } else {
                        onError("Signup failed: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    onError("Network error: ${t.localizedMessage}")
                }
            })
    }
}