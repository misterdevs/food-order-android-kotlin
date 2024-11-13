package com.mrdevs.foodorder.api.models.response

data class OrderHistoryResponse(
    val orderId: Int,
    val totalItem: Int,
    val orderDate: Long?,
    val totalOrder: Int,
    val orderDetails: List<OrderDetailResponse>

)
