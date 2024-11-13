package com.mrdevs.foodorder.api.models.response

data class FoodResponse(
    val foodId: Int,
    val categories: CategoryResponse?,
    val foodName: String,
    val imageFilename: String,
    val price: Int,
    val ingredient: String,
    val isFavorite: Boolean,
    val isCart: Boolean,
    val createdBy: String,
    val createdTime: String,
    val modifiedBy: String,
    val modifiedTime: String
)
