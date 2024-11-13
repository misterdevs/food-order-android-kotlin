package com.mrdevs.foodorder.api.services

import com.mrdevs.foodorder.api.models.request.CheckoutRequest
import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.GlobalList
import com.mrdevs.foodorder.api.models.response.OrderHistoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderService {

    @GET("/api/food-order/history")
    fun getOrderHistories(@Query("pageSize") pageSize: Int? = 8): Call<GlobalList<List<OrderHistoryResponse>>>

    @POST("/api/food-order/cart/checkout")
    fun checkout(@Body cartIds: List<CheckoutRequest>): Call<Global<List<CartResponse>>>
}