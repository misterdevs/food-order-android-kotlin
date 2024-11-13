package com.mrdevs.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.api.models.response.OrderDetailResponse
import com.mrdevs.foodorder.databinding.ItemOrderhistoryBinding
import com.mrdevs.foodorder.tool.Formatter
import com.squareup.picasso.Picasso

class OrderItemAdapter(private val orderDetails: List<OrderDetailResponse>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemListViewHolder {
        return OrderItemListViewHolder(
            ItemOrderhistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderItemListViewHolder, position: Int) {
        val (_, item, imageFilename, qty, price, totalPrice) = orderDetails[position]
        holder.binding.foodName.text = item
        holder.binding.total.text = holder.itemView.context.getString(
            R.string.total,
            qty.toString(),
            Formatter.currency(price)
        )
        holder.binding.totalPrice.text =
            holder.itemView.context.getString(R.string.total_price, Formatter.currency(totalPrice))
        Picasso.get().load(imageFilename).into(holder.binding.foodImage)
    }

    override fun getItemCount(): Int {
        return orderDetails.count()
    }

    class OrderItemListViewHolder(val binding: ItemOrderhistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}