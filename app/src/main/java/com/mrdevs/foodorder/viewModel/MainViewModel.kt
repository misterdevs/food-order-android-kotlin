package com.mrdevs.foodorder.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrdevs.foodorder.api.ApiInstance
import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.api.models.response.FavoriteFoodResponse
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.api.models.response.Global
import com.mrdevs.foodorder.api.models.response.GlobalList
import com.mrdevs.foodorder.api.services.FoodService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class MainViewModel : ViewModel() {

    private var currentCall: Call<*>? = null

    val sortByTitleItems =
        listOf("None", "Food Name A-Z", "Food Name Z-A", "Highest Price", "Lowest Price")
    private val sortByValueItems =
        listOf("", "foodName,asc", "foodName,desc", "price,desc", "price,asc")
    private val sortByFavoriteValueItems = listOf(
        "",
        "foods.foodName,asc",
        "foods.foodName,desc",
        "foods.price,desc",
        "foods.price,asc"
    )

    val filterByCategoryItems = listOf("None", "Master Chef", "Hard", "Normal", "Easy")
    private val filterByCategoryValueItems = listOf(null, 1, 2, 3, 4)

    private val _foodPagination = MutableLiveData<Int>()
    val foodPagination: LiveData<Int> = _foodPagination

    private val _activeFoodPagination = MutableLiveData<Int>()
    val activeFoodPagination: LiveData<Int> = _activeFoodPagination

    private val _favFoodPagination = MutableLiveData<Int>()
    val favFoodPagination: LiveData<Int> = _favFoodPagination

    private val _activeFavFoodPagination = MutableLiveData<Int>()
    val activeFavFoodPagination: LiveData<Int> = _activeFavFoodPagination

    private val _appBarName = MutableLiveData<String>()
    val appBarName: LiveData<String> = _appBarName

    private val _foodList = MutableLiveData<List<FoodResponse>?>()
    val foodList: LiveData<List<FoodResponse>?> = _foodList

    private val _favoriteFoodList = MutableLiveData<List<FavoriteFoodResponse>?>()
    val favoriteFoodList: LiveData<List<FavoriteFoodResponse>?> = _favoriteFoodList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    fun getSortByValue(index: Int): String {
        return sortByValueItems[index]
    }

    fun getSortFavoriteByValue(index: Int): String {
        return sortByFavoriteValueItems[index]
    }

    fun getFilterByCategoryValue(index: Int): Int? {
        return filterByCategoryValueItems[index]
    }

    fun setFoodList(foodList: List<FoodResponse>) {
        _foodList.value = foodList
    }

    fun setFoodPagination(totalPage: Int, pageSize: Int) {
        val count = totalPage / pageSize
        _foodPagination.value = when {
            count > 0 -> count
            else -> 1
        }
    }

    fun setFavFoodPagination(totalPage: Int, pageSize: Int) {
        val count = totalPage / pageSize
        _favFoodPagination.value = when {
            count > 0 -> count
            else -> 1
        }
    }

    fun setActiveFoodPagination(page: Int) {
        _activeFoodPagination.value = page
    }

    fun setActiveFavFoodPagination(page: Int) {
        _activeFavFoodPagination.value = page
    }

    fun setFavoriteFoodList(favoriteFoodList: List<FavoriteFoodResponse>) {
        _favoriteFoodList.value = favoriteFoodList
    }

    fun setAppBarName(name: String) {
        _appBarName.value = name
    }

    fun setIsLoading(boolean: Boolean) {
        _isLoading.value = boolean
    }

    fun setIsError(message: String?) {
        _isError.value = message
    }

    fun getFoods(
        context: Context,
        pageNumber: Int? = 1,
        pageSize: Int? = null,
        categoryId: Int? = null,
        sortBy: String? = null,
        keyword: String? = null
    ) {
        currentCall?.cancel()
        setIsLoading(true)
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        val call = foodService.getFoods(pageNumber, pageSize, categoryId, sortBy, keyword)
        currentCall = call
        call.enqueue(object : Callback<GlobalList<List<FoodResponse>>> {
            override fun onResponse(
                call: Call<GlobalList<List<FoodResponse>>>,
                response: Response<GlobalList<List<FoodResponse>>>
            ) {
                if (response.isSuccessful) {
                    Log.i("FoodListViewModel/GetFoods", "GetFoods Successfully")
                    response.body()?.let { setFoodList(it.data) }
                    if (response.body() !== null) {
                        setFoodPagination(response.body()!!.total, response.body()!!.page.pageSize)
                    }
                } else {

                    val errorMessage = if (response.errorBody() !== null) {
                        val type: Type =
                            object : TypeToken<GlobalList<List<FoodResponse>>>() {}.type
                        val body = Gson().fromJson<GlobalList<List<FoodResponse>>>(
                            response.errorBody()?.string(), type
                        )
                        setFoodList(body.data)
                        body.message
                    } else {
                        response.message()
                    }
                    Log.e("FoodListViewModel/GetFoods", errorMessage)
                    setIsError(errorMessage)
                }
                setIsLoading(false)
                currentCall = null

            }

            override fun onFailure(call: Call<GlobalList<List<FoodResponse>>>, t: Throwable) {
                Log.e("FoodListViewModel/GetFoods", t.stackTraceToString())
                setIsLoading(false)
                currentCall = null
            }
        })
    }

    fun getFavoriteFoods(
        context: Context,
        pageNumber: Int? = 1,
        pageSize: Int? = null,
        categoryId: Int? = null,
        sortBy: String? = null,
        keyword: String? = null
    ) {
        currentCall?.cancel()
        setIsLoading(true)
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        val call = foodService.getFavoriteFoods(pageNumber, pageSize, categoryId, sortBy, keyword)
        currentCall = call
        call.enqueue(object : Callback<GlobalList<List<FavoriteFoodResponse>>> {
            override fun onResponse(
                call: Call<GlobalList<List<FavoriteFoodResponse>>>,
                response: Response<GlobalList<List<FavoriteFoodResponse>>>
            ) {
                if (response.isSuccessful) {
                    Log.i("FoodListViewModel/GetFavoriteFoods", "GetFavoriteFoods Successfully")
                    response.body()?.let { setFavoriteFoodList(it.data) }
                    if (response.body() !== null) {
                        setFavFoodPagination(
                            response.body()!!.total,
                            response.body()!!.page.pageSize
                        )
                    }
                } else {
                    val errorMessage = if (response.errorBody() !== null) {
                        val type: Type =
                            object : TypeToken<GlobalList<List<FavoriteFoodResponse>>>() {}.type
                        val body = Gson().fromJson<GlobalList<List<FavoriteFoodResponse>>>(
                            response.errorBody()?.string(), type
                        )
                        setFavoriteFoodList(body.data)
                        body.message
                    } else {
                        response.message()
                    }
                    Log.e("FoodListViewModel/GetFavoriteFoods", errorMessage)
                    setIsError(errorMessage)
                }
                setIsLoading(false)
                currentCall = null

            }

            override fun onFailure(
                call: Call<GlobalList<List<FavoriteFoodResponse>>>,
                t: Throwable
            ) {
                Log.e("FoodListViewModel/GetFavoriteFoods", t.stackTraceToString())
                setIsLoading(false)
                currentCall = null
            }
        })
    }

    fun toggleFavorite(context: Context, foodId: Int) {
        currentCall?.cancel()
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        val call = foodService.toggleFavorite(foodId)
        currentCall = call
        call.enqueue(object : Callback<Global<FoodResponse>> {
            override fun onResponse(
                call: Call<Global<FoodResponse>>,
                response: Response<Global<FoodResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.i(
                        "FoodListViewModel/ToggleFavorite",
                        "FoodId $foodId successfully update favorite"
                    )
                    _foodList.value = foodList.value?.map { food ->
                        if (food.foodId == foodId) {
                            response.body()?.data!!
                        } else {
                            food
                        }
                    }

                    if (!response.body()?.data!!.isFavorite) {
                        _favoriteFoodList.value =
                            favoriteFoodList.value!!.filterNot { data -> data.foods.foodId == foodId }
                    }
                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("FoodListViewModel/ToggleFavorite", errorMessage)
                }
                currentCall = null
            }

            override fun onFailure(call: Call<Global<FoodResponse>>, t: Throwable) {
                Log.e("FoodListViewModel/ToggleFavorite", t.stackTraceToString())
                currentCall = null
            }
        })
    }

    fun addToCart(context: Context, foodId: Int) {
        currentCall?.cancel()
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        val call = foodService.addToCart(foodId)
        currentCall = call
        call.enqueue(object : Callback<Global<CartResponse>> {
            override fun onResponse(
                call: Call<Global<CartResponse>>,
                response: Response<Global<CartResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.i("FoodListViewModel/AddToCart", "FoodId $foodId successfully add to cart")

                    //update foodList
                    _foodList.value = foodList.value?.map { food ->
                        if (food.foodId == foodId) {
                            response.body()?.data?.foods!!
                        } else {
                            food
                        }
                    }

                    //update favoriteFoodList
                    favoriteFoodList.value?.find { it.foods.foodId == foodId }?.foods =
                        response.body()?.data?.foods!!
                    _favoriteFoodList.value = favoriteFoodList.value

                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("FoodListViewModel/AddToCart", errorMessage)
                }
                currentCall = null
            }

            override fun onFailure(call: Call<Global<CartResponse>>, t: Throwable) {
                Log.e("FoodListViewModel/AddToCart", t.stackTraceToString())
                currentCall = null
            }

        })
    }

    fun deleteFromCart(context: Context, foodId: Int) {
        currentCall?.cancel()
        val foodService: FoodService =
            ApiInstance.createService(context, FoodService::class.java, true)
        val call = foodService.deleteFromCart(foodId)
        currentCall = call
        call.enqueue(object : Callback<Global<CartResponse>> {
            override fun onResponse(
                call: Call<Global<CartResponse>>,
                response: Response<Global<CartResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.i(
                        "FoodListViewModel/DeleteFromCart",
                        "FoodId $foodId successfully deleted from cart"
                    )

                    //update foodList
                    _foodList.value = foodList.value?.map { food ->
                        if (food.foodId == foodId) {
                            response.body()?.data?.foods!!
                        } else {
                            food
                        }
                    }

                    //update favoriteFoodList
                    favoriteFoodList.value?.find { it.foods.foodId == foodId }?.foods =
                        response.body()?.data?.foods!!
                    _favoriteFoodList.value = favoriteFoodList.value
                } else {
                    val errorMessage = if (response.body() !== null) {
                        response.body()!!.message
                    } else {
                        response.message()
                    }
                    Log.e("FoodListViewModel/DeleteFromCart", errorMessage)
                }
                currentCall = null
            }

            override fun onFailure(call: Call<Global<CartResponse>>, t: Throwable) {
                Log.e("FoodListViewModel/DeleteFromCart", t.stackTraceToString())
                currentCall = null
            }

        })
    }
}