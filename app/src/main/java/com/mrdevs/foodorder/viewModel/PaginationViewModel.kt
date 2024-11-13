package com.mrdevs.foodorder.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaginationViewModel : ViewModel() {
    val entries = listOf("8", "16", "48")

    private val _activeEntry = MutableLiveData<String>()
    val activeEntry: LiveData<String> = _activeEntry

    fun setActiveEntry(entry: String) {
        _activeEntry.value = entry
    }

}