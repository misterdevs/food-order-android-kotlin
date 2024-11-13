package com.mrdevs.foodorder.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrdevs.foodorder.api.ApiInstance
import com.mrdevs.foodorder.api.models.response.GlobalList
import com.mrdevs.foodorder.api.models.response.OrderHistoryResponse
import com.mrdevs.foodorder.api.services.OrderService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryListViewModel : ViewModel() {

    private val _orderHistoryList = MutableLiveData<List<OrderHistoryResponse>?>()
    val orderHistoryList: LiveData<List<OrderHistoryResponse>?> = _orderHistoryList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setOrderHistoryList(orderHistoryList: List<OrderHistoryResponse>) {
        _orderHistoryList.value = orderHistoryList
    }

    fun setIsLoading(boolean: Boolean) {
        _isLoading.value = boolean
    }

    fun getOrderHistories(context: Context, pageSize: Int? = null) {
        setIsLoading(true)
        val orderService: OrderService =
            ApiInstance.createService(context, OrderService::class.java, true)
        orderService.getOrderHistories(pageSize)
            .enqueue(object : Callback<GlobalList<List<OrderHistoryResponse>>> {
                override fun onResponse(
                    call: Call<GlobalList<List<OrderHistoryResponse>>>,
                    response: Response<GlobalList<List<OrderHistoryResponse>>>
                ) {
                    if (response.isSuccessful) {
                        Log.i(
                            "OrderHistoryListViewModel/GetOrderHistories",
                            "GetOrderHistories Successfully"
                        )
                        response.body()?.let { setOrderHistoryList(it.data) }
                    } else {
                        val errorMessage = if (response.body() !== null) {
                            response.body()!!.message
                        } else {
                            response.message()
                        }
                        Log.e("OrderHistoryListViewModel/GetOrderHistories", errorMessage)
                    }
                    setIsLoading(false)
                }

                override fun onFailure(
                    call: Call<GlobalList<List<OrderHistoryResponse>>>,
                    t: Throwable
                ) {
                    Log.e("OrderHistoryListViewModel/GetOrderHistories", t.stackTraceToString())
                    setIsLoading(false)
                }
            })
    }
}