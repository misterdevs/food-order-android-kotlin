package com.mrdevs.foodorder.api.models.response

data class LoginResponse(
    val userId: Int,
    val username: String,
    val fullName: String,
    val token: String,
    val role: List<String>
)

