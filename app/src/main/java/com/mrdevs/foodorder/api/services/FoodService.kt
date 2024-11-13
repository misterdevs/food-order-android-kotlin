package com.mrdevs.foodorder.api.services

import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.api.models.response.FavoriteFoodResponse
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.GlobalList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodService {

    @GET("/api/food-order/foods")
    fun getFoods(
        @Query("pageNumber") pageNumber: Int? = 1,
        @Query("pageSize") pageSize: Int? = 8,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("keyword") keyword: String? = null
    ): Call<GlobalList<List<FoodResponse>>>

    @GET("/api/food-order/foods/my-favorite-foods")
    fun getFavoriteFoods(
        @Query("pageNumber") pageNumber: Int? = 1,
        @Query("pageSize") pageSize: Int? = 8,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("keyword") keyword: String? = null
    ): Call<GlobalList<List<FavoriteFoodResponse>>>

    @PUT("/api/food-order/foods/{foodId}/favorites")
    fun toggleFavorite(@Path("foodId") foodId: Int): Call<Global<FoodResponse>>

    @GET("/api/food-order/cart")
    fun getMyCarts(): Call<GlobalList<List<CartResponse>>>

    @POST("/api/food-order/cart")
    fun addToCart(@Body foodId: Int): Call<Global<CartResponse>>

    @PUT("/api/food-order/cart/{cartId}")
    fun updateQtyCartItem(@Path("cartId") cartId: Int, @Body qty: Int): Call<Global<CartResponse>>

    @DELETE("/api/food-order/cart/{foodId}")
    fun deleteFromCart(@Path("foodId") foodId: Int): Call<Global<CartResponse>>

}