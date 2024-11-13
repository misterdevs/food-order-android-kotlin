package com.mrdevs.foodorder.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.mrdevs.foodorder.FilterBottomSheet
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.adapter.EntriesAdapter
import com.mrdevs.foodorder.adapter.FoodListAdapter
import com.mrdevs.foodorder.adapter.PaginationAdapter
import com.mrdevs.foodorder.api.models.response.FavoriteFoodResponse
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.databinding.FragmentFavoritefoodlistBinding
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import com.mrdevs.foodorder.viewModel.FilterViewModel
import com.mrdevs.foodorder.viewModel.MainViewModel
import com.mrdevs.foodorder.viewModel.PaginationViewModel
import java.util.stream.Collectors

class FavoritefoodlistFragment : Fragment() {

    private lateinit var binding: FragmentFavoritefoodlistBinding
    private lateinit var sharedPreferences: SharedPreferenceManager
    private val viewModel: MainViewModel by activityViewModels()

    private val pagViewModel: PaginationViewModel = PaginationViewModel()
    private val filterViewModel: FilterViewModel = FilterViewModel()

    private fun init() {
        sharedPreferences = SharedPreferenceManager(activity?.applicationContext!!)
        viewModel.setAppBarName(resources.getString(R.string.app_bar_name_favorite_food))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritefoodlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


        //observe list of favorite food
        viewModel.favoriteFoodList.observe(viewLifecycleOwner) { foods ->
            if (foods != null) {
                setFavoriteFoodList(foods)
            }
        }

        //observe search keyword
        filterViewModel.searchKeyword.observe(viewLifecycleOwner) {
            setFilter()
        }

        //observe sort by
        filterViewModel.sortBy.observe(viewLifecycleOwner) {
            setFilter()
        }

        //observe filter by category
        filterViewModel.filterByCategory.observe(viewLifecycleOwner) {
            setFilter()
        }

        //observe pagination
        pagViewModel.activeEntry.observe(viewLifecycleOwner) { entry ->
            if (entry !== null) {
                setEntries(entry)
                viewModel.setActiveFavFoodPagination(1)
            }
        }

        viewModel.favFoodPagination.observe(viewLifecycleOwner) { page ->
            if (page !== null) {
                if (viewModel.activeFavFoodPagination.value !== null) {
                    setPagination(page, viewModel.activeFavFoodPagination.value!!)
                } else {
                    viewModel.setActiveFavFoodPagination(1)
                }
            }
        }

        viewModel.activeFavFoodPagination.observe(viewLifecycleOwner) { activePage ->
            if (activePage !== null) {
                setPagination(viewModel.favFoodPagination.value!!, activePage)
                if (viewLifecycleOwner.lifecycle.currentState === Lifecycle.State.RESUMED) {
                    setFilter()
                }
            }

        }

        //observe isLoading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setIsLoading(isLoading)
        }

        //observe is error
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            when {
                isError !== null -> {
                    Toast.makeText(requireContext(), isError, Toast.LENGTH_SHORT).show()
                    viewModel.setIsError(null)
                }
            }
        }

        //init fetch cart list
        viewModel.getFavoriteFoods(requireContext())

        //list of favorite food view
        binding.foodList.layoutManager = GridLayoutManager(requireContext(), 2)

        //init entry
        setEntries("8")

        //handle back button
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //init sort by
        setSortByList()

        //search keyword on enter
        binding.searchBar.setOnKeyListener { _, i, keyEvent ->
            when {
                ((i == KeyEvent.KEYCODE_ENTER) && (keyEvent.action == KeyEvent.ACTION_DOWN)) -> {
                    filterViewModel.setSearchKeyword(binding.searchBar.text.toString())
                    return@setOnKeyListener true
                }

                else -> false
            }
        }

        //sort by onchange handle
        (binding.sortBy.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
            filterViewModel.setSortBy(i)
        }

        //handle on click filter button
        val filterBottomSheet = FilterBottomSheet()
        binding.filterButton.setOnClickListener {
            filterBottomSheet.show(parentFragmentManager, FilterBottomSheet.TAG)
        }

        //handle on click callback bottom sheet filter
        filterBottomSheet.setOnClickCallback(object : FilterBottomSheet.OnClickCallback {
            override fun onApplyButtonClicked(categoryId: Int?) {
                filterViewModel.setFilterByCategory(categoryId)
                filterBottomSheet.dismissNow()
            }

            override fun onClearButtonClicked() {
                filterViewModel.setFilterByCategory(null)
                filterBottomSheet.dismissNow()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        //re-init sort by
        setSortByList()
    }

    private fun setFavoriteFoodList(favoriteFoods: List<FavoriteFoodResponse>) {


        val foods = favoriteFoods.stream().map { data -> data.foods }
            .collect(Collectors.toList()) as List<FoodResponse>
        //assign foods to view
        val adapter = FoodListAdapter(foods)

        adapter.setOnClickCallback(object : FoodListAdapter.OnClickCallback {
            override fun onItemClicked(food: FoodResponse) {

                val bundle = Bundle()
                bundle.putInt("foodId", food.foodId)

                val fragment = FooddetailFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.dashFragmentContainer,
                        fragment,
                        FooddetailFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }

            override fun onStarredButtonClicked(foodId: Int) {
                viewModel.toggleFavorite(requireContext(), foodId)
            }

            override fun onAddToCartButtonClicked(foodId: Int) {
                val food = foods.first { food -> food.foodId == foodId }
                when {
                    food.isCart -> viewModel.deleteFromCart(
                        requireContext(),
                        foodId
                    )

                    else -> viewModel.addToCart(
                        requireContext(),
                        foodId
                    )
                }
            }

        })
        binding.foodList.adapter = adapter
    }


    private fun setEntries(entry: String) {
        val adapter = EntriesAdapter(pagViewModel.entries, entry)
        binding.entriesOption.layoutManager = object : GridLayoutManager(requireContext(), 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        adapter.setOnClickCallback(object : EntriesAdapter.OnClickCallback {
            override fun onItemClicked(entry: String) {
                pagViewModel.setActiveEntry(entry)
            }
        })
        binding.entriesOption.adapter = adapter
    }

    private fun setPagination(pagesLength: Int, activePage: Int) {
        val max = 8
        val adapter = PaginationAdapter(pagesLength, activePage, max)
        val span = when {
            pagesLength >= max -> max
            else -> pagesLength
        }
        binding.pagination.layoutManager = object : GridLayoutManager(requireContext(), span) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        adapter.setOnClickCallback(object : PaginationAdapter.OnClickCallback {
            override fun onItemClicked(page: Int) {
                viewModel.setActiveFavFoodPagination(page)
            }
        })
        binding.pagination.adapter = adapter

    }


    private fun setFilter() {
        val searchKeyword: String? = filterViewModel.searchKeyword.value
        val sortByIndex: Int? = filterViewModel.sortBy.value
        val sortBy = sortByIndex?.let { viewModel.getSortFavoriteByValue(it) }
        val filterByCategoryIndex: Int? = filterViewModel.filterByCategory.value
        val filterByCategory = filterByCategoryIndex?.let { viewModel.getFilterByCategoryValue(it) }
        Log.d(
            "FavoriteFoodlistFragment/Filter",
            "keyword: $searchKeyword, sortBy: $sortBy, filterByCategory: $filterByCategory"
        )
        viewModel.getFavoriteFoods(
            requireContext(),
            viewModel.activeFavFoodPagination.value,
            pagViewModel.activeEntry.value?.toInt(),
            filterByCategory,
            sortBy,
            searchKeyword
        )
    }

    private fun setIsLoading(boolean: Boolean) {
        binding.progressIndicator.isVisible = boolean
    }

    private fun setSortByList() {
        val sortByAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, viewModel.sortByTitleItems)
        (binding.sortBy.editText as? AutoCompleteTextView)?.setAdapter(sortByAdapter)
    }

}