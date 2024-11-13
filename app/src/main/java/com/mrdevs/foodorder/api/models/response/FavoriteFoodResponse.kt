package com.mrdevs.foodorder.api.models.response

data class FavoriteFoodResponse(
    val isFavorite: Boolean,
    val createdBy: String,
    val createdTime: String,
    val modifiedBy: String,
    val modifiedTime: String,
    var foods: FoodResponse
)
