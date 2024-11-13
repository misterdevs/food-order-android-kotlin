package com.mrdevs.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.api.models.response.FoodResponse
import com.mrdevs.foodorder.databinding.CardFoodBinding
import com.mrdevs.foodorder.tool.Formatter
import com.squareup.picasso.Picasso

class FoodListAdapter(private val foodList: List<FoodResponse>) :
    RecyclerView.Adapter<FoodListAdapter.FoodListViewHolder>() {

    private lateinit var onClickCallback: OnClickCallback

    fun setOnClickCallback(food: OnClickCallback) {
        this.onClickCallback = food
    }

    interface OnClickCallback {
        fun onItemClicked(food: FoodResponse)
        fun onStarredButtonClicked(foodId: Int)
        fun onAddToCartButtonClicked(foodId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        return FoodListViewHolder(
            CardFoodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        val (_, category, foodName, imageFilename, price, _, isFavorite, isCart) = foodList[position]
        holder.binding.foodName.text = foodName
        holder.binding.categoryName.text = category?.categoryName
        holder.binding.price.text = Formatter.currency(price)
        holder.binding.starredButton.isChecked = isFavorite
        Picasso.get().load(imageFilename).into(holder.binding.foodImage)

        if (isCart) {
            holder.binding.addToCartButton.text = holder.itemView.context.getString(R.string.cancel)
            holder.binding.addToCartButton.setTextColor(
                ContextCompat.getColorStateList(
                    holder.binding.addToCartButton.context,
                    R.color.green
                )
            )
            holder.binding.addToCartButton.iconTint = ContextCompat.getColorStateList(
                holder.binding.addToCartButton.context,
                R.color.green
            )
            holder.binding.addToCartButton.backgroundTintList = ContextCompat.getColorStateList(
                holder.binding.addToCartButton.context,
                R.color.white
            )
            holder.binding.addToCartButton.strokeWidth = 2
            holder.binding.addToCartButton.strokeColor = ContextCompat.getColorStateList(
                holder.binding.addToCartButton.context,
                R.color.green
            )

        }


        holder.binding.productName.setOnClickListener {
//            Toast.makeText(holder.binding.productName.context,"I Clicked ${foodName}", Toast.LENGTH_SHORT).show()
            onClickCallback.onItemClicked(foodList[holder.adapterPosition])
        }

        holder.binding.starredButton.setOnClickListener {
            onClickCallback.onStarredButtonClicked(foodList[holder.adapterPosition].foodId)
        }

        holder.binding.addToCartButton.setOnClickListener {
            onClickCallback.onAddToCartButtonClicked(foodList[holder.adapterPosition].foodId)
        }
    }

    override fun getItemCount(): Int {
        return foodList.count()
    }

    class FoodListViewHolder(val binding: CardFoodBinding) : RecyclerView.ViewHolder(binding.root)
}