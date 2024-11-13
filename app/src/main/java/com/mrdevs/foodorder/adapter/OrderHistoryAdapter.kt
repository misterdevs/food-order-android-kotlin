package com.mrdevs.foodorder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.api.models.response.OrderHistoryResponse
import com.mrdevs.foodorder.databinding.CardOrderhistoryBinding
import com.mrdevs.foodorder.tool.Formatter

class OrderHistoryAdapter(private val orderHistories: List<OrderHistoryResponse>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryListViewHolder {
        return OrderHistoryListViewHolder(
            CardOrderhistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderHistoryListViewHolder, position: Int) {
        val (orderId, totalItem, orderDate, totalOrder, orderDetails) = orderHistories[position]
        holder.binding.orderId.text =
            holder.itemView.context.getString(R.string.order_id, orderId.toString())
        if (orderDate != null) {
            holder.binding.orderDate.text = Formatter.date(orderDate)
        }
        holder.binding.totalItem.text =
            holder.itemView.context.getString(R.string.total_item, totalItem.toString())
        holder.binding.totalOrder.text =
            holder.itemView.context.getString(R.string.total_order, Formatter.currency(totalOrder))

        holder.binding.orderDetailList.layoutManager =
            GridLayoutManager(holder.binding.orderDetailList.context, 1)
        holder.binding.orderDetailList.adapter = OrderItemAdapter(orderDetails)

        if (position == 0) {
            holder.binding.orderDetailList.visibility = View.VISIBLE
            holder.binding.bottomDivider.visibility = View.VISIBLE
            holder.binding.expandButton.text = holder.itemView.context.getString(R.string.show_less)
        }

        holder.binding.expandButton.setOnClickListener {

            if (holder.binding.orderDetailList.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.orderDetailList.visibility = View.VISIBLE
                holder.binding.bottomDivider.visibility = View.VISIBLE
                holder.binding.expandButton.text =
                    holder.itemView.context.getString(R.string.show_less)
            } else {
                holder.binding.orderDetailList.visibility = View.GONE
                holder.binding.bottomDivider.visibility = View.GONE
                holder.binding.expandButton.text =
                    holder.itemView.context.getString(R.string.show_more)
            }
        }


    }

    override fun getItemCount(): Int {
        return orderHistories.count()
    }


    class OrderHistoryListViewHolder(val binding: CardOrderhistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}