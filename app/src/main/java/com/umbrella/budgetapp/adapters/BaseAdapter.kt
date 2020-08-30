package com.umbrella.budgetapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {

    private lateinit var binding: Bind<T>

    interface CallBack {
        fun onItemClick(itemId: Long)
    }

    interface Bind<T> {
        fun onBinding(item: T, itemView: View, adapterPosition: Int)
    }

    fun onBind(bind: Bind<T>) {
        binding = bind
    }

    abstract fun setData(list: List<T>)

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: T) {
            binding.onBinding(item, itemView, adapterPosition)
        }
    }
}
