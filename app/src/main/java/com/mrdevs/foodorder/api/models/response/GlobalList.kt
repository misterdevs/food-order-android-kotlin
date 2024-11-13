package com.mrdevs.foodorder.api.models.response

import java.util.Objects

data class GlobalList<T>(
    val status: Int,
    val message: String,
    val data: T,
    val error: String,
    val total: Int,
    val page: PageResponse
)
