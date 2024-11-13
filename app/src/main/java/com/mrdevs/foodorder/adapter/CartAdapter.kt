package com.mrdevs.foodorder.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrdevs.foodorder.api.models.response.CartResponse
import com.mrdevs.foodorder.databinding.CardCartBinding
import com.mrdevs.foodorder.tool.Formatter
import java.util.Timer
import java.util.TimerTask


class CartAdapter(private val myCarts: List<CartResponse>, private val selectedCarts: List<Int>?) :
    RecyclerView.Adapter<CartAdapter.CartListViewHolder>() {

    private lateinit var onClickCallback: OnClickCallback

    fun setOnClickCallback(item: OnClickCallback) {
        this.onClickCallback = item
    }

    interface OnClickCallback {
        fun onItemChecked(item: CartResponse)
        fun onDeleteClicked(item: CartResponse)
        fun onQtyChanged(item: CartResponse, input: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder(
            CardCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        val cart = myCarts[position]
        holder.binding.foodName.text = cart.foods.foodName
        holder.binding.price.text = Formatter.currency(cart.foods.price)
        holder.binding.totalPrice.text = Formatter.currency(cart.foods.price * cart.qty)
        holder.binding.qty.setText(cart.qty.toString())
        holder.binding.itemCheckbutton.isChecked =
            !selectedCarts?.filter { cartId -> cartId == cart.cartId }.isNullOrEmpty()


        holder.binding.qty.addTextChangedListener(object : TextWatcher {
            var timer = Timer()
            override fun afterTextChanged(input: Editable?) {
                timer = Timer()
                timer.schedule(object : TimerTask() {

                    override fun run() {
                        if (!input.isNullOrEmpty() && input.toString().toInt() > 0) {
                            onClickCallback.onQtyChanged(cart, input.toString())
                        } else {
                            holder.binding.qty.setText("1")
                            onClickCallback.onQtyChanged(cart, input.toString())
                        }
                    }
                }, 600)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                timer.cancel()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        holder.binding.itemCheckbutton.setOnClickListener {
            onClickCallback.onItemChecked(cart)
        }
        holder.binding.increaseQtyButton.setOnClickListener {
            val value = holder.binding.qty.text.toString().toInt() + 1
            holder.binding.qty.setText(value.toString())
        }
        holder.binding.decreaseQtyButton.setOnClickListener {
            if (holder.binding.qty.text.toString().toInt() > 0) {
                val value = holder.binding.qty.text.toString().toInt() - 1
                holder.binding.qty.setText(value.toString())
            }

        }
        holder.binding.deleteButton.setOnClickListener {
            onClickCallback.onDeleteClicked(cart)
        }
    }

    override fun getItemCount(): Int {
        return myCarts.count()
    }

    class CartListViewHolder(val binding: CardCartBinding) : RecyclerView.ViewHolder(binding.root)
}