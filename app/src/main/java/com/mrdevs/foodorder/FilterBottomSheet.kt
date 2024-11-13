package com.mrdevs.foodorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mrdevs.foodorder.databinding.BottomSheetFilterBinding
import com.mrdevs.foodorder.viewModel.FilterViewModel
import com.mrdevs.foodorder.viewModel.MainViewModel

class FilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFilterBinding
    private val mainViewModel: MainViewModel = MainViewModel()
    private val filterViewModel: FilterViewModel = FilterViewModel()

    private lateinit var onClickCallback: OnClickCallback

    fun setOnClickCallback(callback: OnClickCallback) {
        this.onClickCallback = callback
    }

    interface OnClickCallback {
        fun onApplyButtonClicked(categoryId: Int?)
        fun onClearButtonClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init filter by category
        setFilterByCategoryList()

        //handle filter by category
        (binding.filterByCategory.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
            filterViewModel.setFilterByCategory(i)
        }

        binding.applyButton.setOnClickListener {
            onClickCallback.onApplyButtonClicked(filterViewModel.filterByCategory.value)
        }

        binding.clearButton.setOnClickListener {
            binding.filterByCategory.editText?.id = 0
            filterViewModel.setFilterByCategory(null)
            onClickCallback.onClearButtonClicked()
        }
    }

    override fun onResume() {
        super.onResume()

        //re-init filter by category
        setFilterByCategoryList()
    }

    private fun setFilterByCategoryList() {
        val filterByCategoryAdapter = ArrayAdapter(requireContext(), R.layout.list_item, mainViewModel.filterByCategoryItems)
        (binding.filterByCategory.editText as? AutoCompleteTextView)?.setAdapter(
            filterByCategoryAdapter
        )
    }

}