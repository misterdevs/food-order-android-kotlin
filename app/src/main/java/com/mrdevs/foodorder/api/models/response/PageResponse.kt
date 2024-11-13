package com.mrdevs.foodorder.api.models.response

data class PageResponse(
    val pageNumber: Int,
    val pageSize:Int,
    val offset:Int
)
