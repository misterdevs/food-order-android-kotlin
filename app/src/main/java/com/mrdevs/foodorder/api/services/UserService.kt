package com.mrdevs.foodorder.api.services

import com.mrdevs.foodorder.api.models.request.UserLoginRequest
import com.mrdevs.foodorder.api.models.request.UserRegisterRequest
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.LoginResponse
import com.mrdevs.foodorder.api.models.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("/api/user-management/users/sign-in")
    fun login(@Body user: UserLoginRequest): Call<Global<LoginResponse>>

    @POST("/api/user-management/users/sign-up")
    fun register(@Body user: UserRegisterRequest): Call<Global<UserResponse>>
}