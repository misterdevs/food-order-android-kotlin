package com.mrdevs.foodorder.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrdevs.foodorder.api.ApiInstance
import com.mrdevs.foodorder.api.models.request.CheckoutRequest
import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.GlobalList
import com.mrdevs.foodorder.api.services.FoodService
import com.mrdevs.foodorder.api.services.OrderService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors

class CartListViewModel : ViewModel() {

    private val _cartList = MutableLiveData<List<CartResponse>?>()
    val cartList: LiveData<List<CartResponse>?> = _cartList

    private val _selectedCartItems = MutableLiveData<List<Int>?>()
    val selectedCartItems: LiveData<List<Int>?> = _selectedCartItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setCartList(cartList: List<CartResponse>?) {
        _cartList.value = cartList
    }

    fun setSelectedCartItems(cartIds: List<Int>) {
        _selectedCartItems.value = cartIds
    }

    fun setSelectedCartItem(cartId: Int) {
        if (_selectedCartItems.value == null) {
            val items = ArrayList<Int>()
            items.add(cartId)
            _selectedCartItems.value = items
        } else {
            val items = _selectedCartItems.value?.stream()?.collect(Collectors.toList())
            items!!.add(cartId)
            _selectedCartItems.value = items
        }
    }

    fun deleteSelectedCartItem(cartId: Int) {
        if (_selectedCartItems.value !== null) {
            val items = selectedCartItems.value?.filterNot { id -> id == cartId }
            _selectedCartItems.value = items
        }

    }

    fun setIsLoading(boolean: Boolean) {
        _isLoading.value = boolean
    }


    fun getMyCarts(context: Context) {
        setIsLoading(true)
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        foodService.getMyCarts().enqueue(object :
            Callback<GlobalList<List<CartResponse>>> {
            override fun onResponse(
                call: Call<GlobalList<List<CartResponse>>>,
                response: Response<GlobalList<List<CartResponse>>>
            ) {
                if (response.isSuccessful) {
                    Log.i("CartListViewModel/GetMyCarts", "GetMyCarts Successfully")
                    response.body()?.let { setCartList(it.data) }
                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("CartListViewModel/GetMyCarts", errorMessage)
                }
                setIsLoading(false)
            }

            override fun onFailure(
                call: Call<GlobalList<List<CartResponse>>>,
                t: Throwable
            ) {
                Log.e("CartListViewModel/GetMyCarts", t.stackTraceToString())
                setIsLoading(false)
            }
        })
    }

    fun updateQtyCartItem(context: Context, cartId: Int, qty: Int) {
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        foodService.updateQtyCartItem(cartId, qty).enqueue(object :
            Callback<Global<CartResponse>> {
            override fun onResponse(
                call: Call<Global<CartResponse>>,
                response: Response<Global<CartResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.i("CartListViewModel/UpdateQtyCartItem", "UpdateQtyCartItem Successfully")
                    _cartList.value = cartList.value?.stream()?.map { cart ->
                        if (cart.cartId == cartId) {
                            response.body()?.data as CartResponse
                        } else {
                            cart
                        }
                    }?.collect(Collectors.toList())
                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("CartListViewModel/UpdateQtyCartItem", errorMessage)
                }
            }

            override fun onFailure(
                call: Call<Global<CartResponse>>,
                t: Throwable
            ) {
                Log.e("CartListViewModel/UpdateQtyCartItem", t.stackTraceToString())
            }
        })
    }

    fun deleteCartItem(context: Context, foodId: Int, mainViewModel: MainViewModel) {
        setIsLoading(true)
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        foodService.deleteFromCart(foodId).enqueue(object :
            Callback<Global<CartResponse>> {
            override fun onResponse(
                call: Call<Global<CartResponse>>,
                response: Response<Global<CartResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.i("CartListViewModel/DeleteQtyCartItem", "DeleteQtyCartItem Successfully")
                    _selectedCartItems.value =
                        selectedCartItems.value?.filterNot { cartId -> cartId == response.body()?.data?.cartId }
                    _cartList.value =
                        cartList.value?.filterNot { cart -> cart.foods.foodId == foodId }
                    _selectedCartItems.value = selectedCartItems.value
                    mainViewModel.setFoodList(mainViewModel.foodList.value?.stream()?.map { food ->
                        if (food.foodId == foodId) {
                            food.copy(isCart = false)

                        } else {
                            food
                        }
                    }?.collect(Collectors.toList()) as List<FoodResponse>)
                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("CartListViewModel/DeleteQtyCartItem", errorMessage)
                }
                setIsLoading(false)
            }

            override fun onFailure(
                call: Call<Global<CartResponse>>,
                t: Throwable
            ) {
                Log.e("CartListViewModel/DeleteQtyCartItem", t.stackTraceToString())
                setIsLoading(false)
            }
        })
    }

    fun calculateTotalOrder(selectedCartItems: List<Int>): Int {
        return selectedCartItems.fold(0) { acc, i ->
            val cart = cartList.value?.first { cart -> cart.cartId == i }
            acc + (cart!!.foods.price * cart.qty)
        }.toInt()
    }

    fun checkout(context: Context, selectedCartItems: List<Int>, mainViewModel: MainViewModel) {
        val orderService: OrderService =
            ApiInstance.createService(context, OrderService::class.java, true)

        val checkoutList = selectedCartItems.map { CheckoutRequest(it) }
        orderService.checkout(checkoutList).enqueue(object : Callback<Global<List<CartResponse>>> {
            override fun onResponse(
                call: Call<Global<List<CartResponse>>>,
                response: Response<Global<List<CartResponse>>>
            ) {
                if (response.isSuccessful) {

                    Log.i("CartListViewModel/Checkout", "Checkout Successfully")
                    _selectedCartItems.value = listOf()
                    response.body()?.data?.forEach {
                        _cartList.value =
                            cartList.value?.filterNot { cart -> cart.cartId == it.cartId }
                        mainViewModel.setFoodList(
                            mainViewModel.foodList.value?.stream()?.map { food ->
                                if (food.foodId == it.foods.foodId) {
                                    food.copy(isCart = false)
                                } else {
                                    food
                                }
                            }?.collect(Collectors.toList()) as List<FoodResponse>
                        )
                    }
                    _selectedCartItems.value = listOf()


                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("CartListViewModel/Checkout", errorMessage)
                }
            }

            override fun onFailure(call: Call<Global<List<CartResponse>>>, t: Throwable) {
                Log.e("CartListViewModel/Checkout", t.stackTraceToString())
            }
        })
    }
}