package com.mrdevs.foodorder.api.models.response

data class OrderDetailResponse(
    val orderDetailId: Int,
    val item: String,
    val imageFilename: String,
    val qty: Int,
    val price: Int,
    val totalPrice: Int

)
