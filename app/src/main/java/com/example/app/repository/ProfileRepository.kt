package com.example.app.repository

import com.example.app.api.ApiClient
import com.example.app.models.profile.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {
    fun getProfile(
        onResult: (UserProfileResponse?) -> Unit,
        onError: (String?) -> Unit
    ) {
        ApiClient.userService.getUserProfile()
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        onError(errorMsg ?: "Failed to fetch profile: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    onError("Network error: ${t.localizedMessage}")
                }
            })
    }
}