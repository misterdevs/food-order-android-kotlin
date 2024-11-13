package com.mrdevs.foodorder.api.models.response

data class CartResponse(
    val cartId: Int,
    val userId: Int,
    val qty: Int,
    val isDeleted: Boolean,
    val createdBy: String,
    val createdTime: String,
    val modifiedBy: String,
    val modifiedTime: String,
    val foods: FoodResponse
)
