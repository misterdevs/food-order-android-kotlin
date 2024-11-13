package com.mrdevs.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mrdevs.foodorder.R
import com.mrdevs.foodorder.databinding.ListEntriesBinding

class EntriesAdapter(private val entries: List<String>, private val activeEntry: String) :
    RecyclerView.Adapter<EntriesAdapter.EntriesViewHolder>() {

    private lateinit var onClickCallback: OnClickCallback

    fun setOnClickCallback(entry: OnClickCallback) {
        this.onClickCallback = entry
    }

    interface OnClickCallback {
        fun onItemClicked(entry: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesViewHolder {
        return EntriesViewHolder(
            ListEntriesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EntriesViewHolder, position: Int) {
        val entry = entries[position]
        holder.binding.entryNumber.text = entry

        if (entry == activeEntry) {
            holder.binding.entryButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.entryButton.context,
                    R.color.green
                )
            )
            holder.binding.entryNumber.setTextColor(
                ContextCompat.getColor(
                    holder.binding.entryNumber.context,
                    R.color.white
                )
            )
        } else {
            holder.binding.entryButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.entryButton.context,
                    R.color.transparent
                )
            )
        }

        holder.binding.entryButton.setOnClickListener {
            onClickCallback.onItemClicked(entry)
        }

    }

    override fun getItemCount(): Int {
        return entries.count()
    }

    class EntriesViewHolder(val binding: ListEntriesBinding) : RecyclerView.ViewHolder(binding.root)
}