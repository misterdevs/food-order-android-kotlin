package com.mrdevs.foodorder.api.models.request

data class FoodRequest(
    val pageSize: Int? = 8,
    val page: Int? = 1,
    val sortBy: String? = ""
)
