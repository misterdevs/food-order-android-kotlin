package com.mrdevs.foodorder.api.models.response

data class CategoryResponse(
    val categoryId: Int,
    val categoryName: String,
    val isDeleted: Boolean
)
