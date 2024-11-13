package com.mrdevs.foodorder.api.models.response

data class Global<T>(
    val status: Int,
    val message: String,
    val error: String,
    val data: T
)
