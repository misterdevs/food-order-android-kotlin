package com.mrdevs.foodorder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.adapter.OrderHistoryAdapter
import com.mrdevs.foodorder.api.models.response.OrderHistoryResponse
import com.mrdevs.foodorder.databinding.FragmentOrderhistoryBinding
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import com.mrdevs.foodorder.viewModel.MainViewModel
import com.mrdevs.foodorder.viewModel.OrderHistoryListViewModel
import com.mrdevs.foodorder.viewModel.PaginationViewModel

class OrderhistoryFragment : Fragment() {
    private lateinit var binding: FragmentOrderhistoryBinding
    private lateinit var sharedPreferences: SharedPreferenceManager

    private val viewModel: OrderHistoryListViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val pagViewModel = PaginationViewModel()

    private fun init() {
        sharedPreferences = SharedPreferenceManager(activity?.applicationContext!!)
        mainViewModel.setAppBarName(resources.getString(R.string.app_bar_name_order_history))
        pagViewModel.setActiveEntry("8")
        binding.showMoreButton.isVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderhistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //observe list of order history
        viewModel.orderHistoryList.observe(viewLifecycleOwner) { orderHistories ->
            if (orderHistories !== null) {
                setOrderHistoryList(orderHistories)
                binding.showMoreButton.isVisible = true
            }
        }

        //observe entries
        pagViewModel.activeEntry.observe(viewLifecycleOwner) { entry ->
            viewModel.getOrderHistories(requireContext(), entry.toInt())
        }

        //observe isLoading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setIsLoading(isLoading)
        }

        //init fetch order history list
        viewModel.getOrderHistories(requireContext())

        //list of order history view
        binding.orderHistoryList.layoutManager = GridLayoutManager(requireContext(), 1)

        //handle backButton
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //handle showMoreButton
        binding.showMoreButton.setOnClickListener {
            moreHistories()

        }
    }

    private fun setOrderHistoryList(orderHistories: List<OrderHistoryResponse>) {
        val adapter = OrderHistoryAdapter(orderHistories)
        binding.orderHistoryList.adapter = adapter
    }

    private fun moreHistories() {
        pagViewModel.setActiveEntry((pagViewModel.activeEntry.value?.toInt()!! + 8).toString())
    }

    private fun setIsLoading(boolean: Boolean) {
        binding.progressIndicator.isVisible = boolean
    }
}