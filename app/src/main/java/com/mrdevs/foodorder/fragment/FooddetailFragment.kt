package com.mrdevs.foodorder.fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.databinding.FragmentFooddetailBinding
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import com.mrdevs.foodorder.tool.Formatter
import com.mrdevs.foodorder.viewModel.MainViewModel
import com.squareup.picasso.Picasso

class FooddetailFragment : Fragment() {

    private lateinit var binding: FragmentFooddetailBinding
    private lateinit var sharedPreferences: SharedPreferenceManager
    private val viewModel: MainViewModel by activityViewModels()

    private fun init() {
        sharedPreferences = SharedPreferenceManager(activity?.applicationContext!!)
        viewModel.setAppBarName(resources.getString(R.string.app_bar_name_detail))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFooddetailBinding.inflate(inflater, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        val foodId: Int? = this.arguments?.getInt("foodId")

        //initial foodlist
        getFood(viewModel.foodList.value, foodId)

        //update foodlist if any update
        viewModel.foodList.observe(viewLifecycleOwner) { foods ->
            getFood(foods, foodId)
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.starredButton.setOnCheckedChangeListener { _, _ ->
            if (foodId !== null) {
                viewModel.toggleFavorite(requireContext(), foodId)
            }

        }

    }

    private fun getFood(foodList: List<FoodResponse>?, foodId: Int?) {
        val food = foodList?.first { food -> food.foodId == foodId }
        Log.d("FoodDetailActivity", food.toString())
        if (food !== null) {
            binding.foodName.text = food.foodName
            binding.price.text = Formatter.currency(food.price)
            binding.categoryName.text = food.categories?.categoryName
            binding.ingredients.text = Html.fromHtml(food.ingredient.formattedHtml())
            binding.starredButton.isChecked = food.isFavorite
            Picasso.get().load(food.imageFilename).into(binding.foodImage)

            Log.d("FoodDetailActivity", food.isCart.toString())

            if (food.isCart) {
                binding.addToCartButton.text = resources.getString(R.string.cancel)
                binding.addToCartButton.setTextColor(
                    ContextCompat.getColorStateList(
                        binding.addToCartButton.context,
                        R.color.green
                    )
                )
                binding.addToCartButton.iconTint =
                    ContextCompat.getColorStateList(binding.addToCartButton.context, R.color.green)
                binding.addToCartButton.backgroundTintList =
                    ContextCompat.getColorStateList(binding.addToCartButton.context, R.color.white)
                binding.addToCartButton.strokeWidth = 2
                binding.addToCartButton.strokeColor =
                    ContextCompat.getColorStateList(binding.addToCartButton.context, R.color.green)
            } else {
                binding.addToCartButton.text =
                    ContextCompat.getString(requireContext(), R.string.addToCart)
                binding.addToCartButton.setTextColor(
                    ContextCompat.getColorStateList(
                        binding.addToCartButton.context,
                        R.color.white
                    )
                )
                binding.addToCartButton.iconTint =
                    ContextCompat.getColorStateList(binding.addToCartButton.context, R.color.white)
                binding.addToCartButton.backgroundTintList =
                    ContextCompat.getColorStateList(binding.addToCartButton.context, R.color.green)
                binding.addToCartButton.strokeWidth = 0
            }

            binding.addToCartButton.setOnClickListener {
                when {
                    food.isCart -> viewModel.deleteFromCart(
                        requireContext(),
                        food.foodId
                    )

                    else -> viewModel.addToCart(
                        requireContext(),
                        food.foodId
                    )
                }
            }
        }

    }

    private fun String.formattedHtml(): String {
        return this
    }


}