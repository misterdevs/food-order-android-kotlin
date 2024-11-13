package com.mrdevs.foodorder.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.databinding.ListPaginationBinding

class PaginationAdapter(
    private val pagesLength: Int,
    private val activePage: Int,
    private val max: Int
) :
    RecyclerView.Adapter<PaginationAdapter.EntriesViewHolder>() {

    private lateinit var onClickCallback: OnClickCallback

    fun setOnClickCallback(entry: OnClickCallback) {
        this.onClickCallback = entry
    }

    interface OnClickCallback {
        fun onItemClicked(entry: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesViewHolder {
        return EntriesViewHolder(
            ListPaginationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EntriesViewHolder, position: Int) {

        val pages = mutableListOf<String>()
        for (i in 0 until pagesLength) {
            val number = i + 1
            if (pagesLength > max) {
                if (activePage <= pagesLength - (max - 1) && (number == activePage || number == activePage - 1 || number == activePage + 1 || number == pagesLength || (number < pagesLength && number > activePage + 1 && pages.size < max - 1))) {
                    if (pages.size == max - 2) {
                        pages.add("...")
                    } else {
                        pages.add(number.toString())
                    }
                } else if (activePage > pagesLength - (max - 1) && (number > pagesLength - max || number == activePage - 1)) {
                    pages.add(number.toString())
                }
            } else {
                pages.add(number.toString())
            }

        }
        val page = pages[position]
        holder.binding.pageNumber.text = page

        if (position != max - 2 && page.toInt() == activePage) {
            holder.binding.pageButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.pageButton.context,
                    R.color.green
                )
            )
            holder.binding.pageNumber.setTextColor(
                ContextCompat.getColor(
                    holder.binding.pageNumber.context,
                    R.color.white
                )
            )
        } else {
            holder.binding.pageButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.pageButton.context,
                    R.color.transparent
                )
            )
        }

        holder.binding.pageButton.setOnClickListener {
            if (position != max - 2) onClickCallback.onItemClicked(page.toInt())
        }

    }

    override fun getItemCount(): Int {
        return when {
            pagesLength >= max -> max
            else -> pagesLength
        }
    }

    class EntriesViewHolder(val binding: ListPaginationBinding) :
        RecyclerView.ViewHolder(binding.root)
}