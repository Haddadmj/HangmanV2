package com.mohammad.hangman

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohammad.hangman.databinding.ItemRowBinding


class RVAdapter(val messages: ArrayList<String>) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]

        holder.binding.apply {
            if (message.startsWith("Wrong") || (message.startsWith("No")))
                tvMessage.setTextColor(Color.RED)
            else if (message.startsWith("Found"))
                tvMessage.setTextColor(Color.GREEN)
            else
                tvMessage.setTextColor(Color.BLACK)

            tvMessage.text = message
        }
    }

    override fun getItemCount(): Int = messages.size

    fun add(s: String) {
        messages.add(s)
        notifyItemChanged(messages.size - 1)
    }

}
