package com.mrdevs.foodorder.api.models.request

data class UserRegisterRequest(
    val username: String,
    val fullName: String,
    val password: String,
    val retypePassword: String
)