package com.mrdevs.foodorder.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.adapter.CartAdapter
import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.databinding.FragmentCartBinding
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import com.mrdevs.foodorder.tool.Formatter
import com.mrdevs.foodorder.viewModel.CartListViewModel
import com.mrdevs.foodorder.viewModel.MainViewModel

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var sharedPreferences: SharedPreferenceManager
    private val viewModel: CartListViewModel = CartListViewModel()
    private val mainViewModel: MainViewModel by activityViewModels()

    private fun init() {
        sharedPreferences = SharedPreferenceManager(activity?.applicationContext!!)
        mainViewModel.setAppBarName(resources.getString(R.string.app_bar_name_my_cart))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //observe list of cart
        viewModel.cartList.observe(viewLifecycleOwner) { carts ->
            setCartList(carts!!, viewModel.selectedCartItems.value)
            setTotalOrder(viewModel.selectedCartItems.value)
        }

        //observe list of selected cart item
        viewModel.selectedCartItems.observe(viewLifecycleOwner) { items ->
            if (items !== null) {
                setCartList(viewModel.cartList.value!!, items)
                setTotalOrder(viewModel.selectedCartItems.value)
                binding.selectAllCheckbutton.isChecked =
                    viewModel.cartList.value!!.size == items.size
            }
        }

        //observe isLoading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setIsLoading(isLoading)
        }

        //init fetch cart list
        viewModel.getMyCarts(requireContext())

        //list of cart view
        binding.cartList.layoutManager = GridLayoutManager(requireContext(), 1)

        //handle back button
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //handle select all button
        binding.selectAllCheckbutton.setOnClickListener {
            when {
                binding.selectAllCheckbutton.isChecked -> viewModel.setSelectedCartItems(viewModel.cartList.value?.map { cart -> cart.cartId }!!)

                !binding.selectAllCheckbutton.isChecked && viewModel.cartList.value!!.size == viewModel.selectedCartItems.value!!.size -> viewModel.setSelectedCartItems(
                    listOf()
                )
            }
        }

        //handle checkout button
        binding.checkoutButton.setOnClickListener {
            checkout(viewModel.selectedCartItems.value!!)
        }
    }

    private fun setCartList(cartList: List<CartResponse>, selectedCartItems: List<Int>?) {
        val adapter = CartAdapter(cartList, selectedCartItems)

        adapter.setOnClickCallback(object : CartAdapter.OnClickCallback {
            override fun onItemChecked(item: CartResponse) {
                when {
                    selectedCartItems?.filter { cartId -> cartId == item.cartId }
                        .isNullOrEmpty() -> viewModel.setSelectedCartItem(item.cartId)

                    else -> viewModel.deleteSelectedCartItem(item.cartId)
                }

            }

            override fun onQtyChanged(item: CartResponse, input: String) {
                if (input.isNotEmpty() && input.toInt() > 0) {
                    viewModel.updateQtyCartItem(
                        requireContext(), item.cartId, input.toInt()
                    )
                }
            }

            override fun onDeleteClicked(item: CartResponse) {
                Log.d("CartFragment", item.toString())
                viewModel.deleteCartItem(
                    requireContext(), item.foods.foodId, mainViewModel
                )
            }
        })

        binding.cartList.adapter = adapter
    }

    private fun setTotalOrder(selectedCartItems: List<Int>?) {
        val value = if (selectedCartItems.isNullOrEmpty()) {
            0
        } else {
            viewModel.calculateTotalOrder(selectedCartItems)
        }
        binding.totalOrder.text = Formatter.currency(value)

    }

    private fun checkout(selectedCartItems: List<Int>) {
        viewModel.checkout(requireContext(), selectedCartItems, mainViewModel)
    }

    private fun setIsLoading(boolean: Boolean) {
        binding.progressIndicator.isVisible = boolean
    }
}