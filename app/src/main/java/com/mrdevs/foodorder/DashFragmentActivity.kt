package com.mrdevs.foodorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mrdevs.foodorder.api.ApiAuthInterceptor
import com.mrdevs.foodorder.databinding.ActivityDashFragmentBinding
import com.mrdevs.foodorder.fragment.CartFragment
import com.mrdevs.foodorder.fragment.FavoritefoodlistFragment
import com.mrdevs.foodorder.fragment.FoodlistFragment
import com.mrdevs.foodorder.fragment.OrderhistoryFragment
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import com.mrdevs.foodorder.viewModel.MainViewModel

class DashFragmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashFragmentBinding
    private lateinit var sharedPreferences: SharedPreferenceManager
    private val viewModel: MainViewModel by viewModels()


    private fun init() {
        binding = ActivityDashFragmentBinding.inflate(layoutInflater)
        sharedPreferences = SharedPreferenceManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContentView(binding.root)

        //observe app bar name
        viewModel.appBarName.observe(this) { name ->
            binding.appbar.appName.text = name
        }

        //init fetch food list
        viewModel.getFoods(this)

        val fragManager = supportFragmentManager
        val foodListFragment = FoodlistFragment()

        val fragment = fragManager.findFragmentByTag(FoodlistFragment::class.java.simpleName)

        if (fragment !is FoodlistFragment) {
            fragManager
                .beginTransaction()
                .add(
                    R.id.dashFragmentContainer,
                    foodListFragment,
                    FoodlistFragment::class.java.simpleName
                )
                .commit()
        }

        //initialize navigation drawer
        val navHeader: View = binding.navigationDrawer.getHeaderView(0)
        val name: TextView = navHeader.findViewById(R.id.name)
        val email: TextView = navHeader.findViewById(R.id.username)

        name.text = sharedPreferences.getFullName()
        email.text = resources.getString(R.string.usernameTag, sharedPreferences.getUsername())


        binding.navigationDrawer.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            when (item.itemId) {
                R.id.orderHistoryItemMenu -> {
                    val orderHistoryFragment = OrderhistoryFragment()
                    fragManager.beginTransaction().apply {
                        replace(
                            R.id.dashFragmentContainer,
                            orderHistoryFragment,
                            OrderhistoryFragment::class.java.simpleName
                        )
                        addToBackStack(null)
                        commit()
                    }
                }

                R.id.favoriteFoodItemMenu -> {
                    val favoriteFoodListFragment = FavoritefoodlistFragment()
                    fragManager.beginTransaction().apply {
                        replace(
                            R.id.dashFragmentContainer,
                            favoriteFoodListFragment,
                            favoriteFoodListFragment::class.java.simpleName
                        )
                        addToBackStack(null)
                        commit()
                    }
                }

                R.id.signOutItemMenu -> {
                    ApiAuthInterceptor(this).logout()
                }
            }
            binding.drawerLayout.close()
            true
        }


        //handling menuButton
        binding.appbar.menuButton.setOnClickListener {
            binding.drawerLayout.open()
        }

        //handling cartButton
        binding.appbar.cartButton.setOnClickListener {
            val cartFragment = CartFragment()
            fragManager.beginTransaction().apply {
                replace(
                    R.id.dashFragmentContainer,
                    cartFragment,
                    CartFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }

        //handling app icon button
        binding.appbar.appIcon.setOnClickListener {
            backToBase()
        }

        //handling app name button
        binding.appbar.appName.setOnClickListener {
            backToBase()
        }
    }

    private fun backToBase() {
        val intent = Intent(this, DashFragmentActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}