package com.mrdevs.foodorder.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FilterViewModel {

    private val _searchKeyword = MutableLiveData<String?>()
    val searchKeyword: LiveData<String?> = _searchKeyword

    private val _filterByCategory = MutableLiveData<Int?>()
    val filterByCategory: LiveData<Int?> = _filterByCategory

    private val _sortBy = MutableLiveData<Int>()
    val sortBy: LiveData<Int> = _sortBy


    fun setSearchKeyword(keyword: String? = null) {
        _searchKeyword.value = keyword
    }

    fun setSortBy(sortBy: Int) {
        _sortBy.value = sortBy
    }

    fun setFilterByCategory(index: Int?) {
        _filterByCategory.value = index
    }
}