package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_reorderable_view.view.*
import kotlin.properties.Delegates

class CategoriesAdapter(val callback: CallBack) : BaseAdapter<Category>() {

    var categories : List<Category> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_reorderable_view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(categories[position])
        holder.itemView.id = categories[position].id!!.toInt()
    }

    override fun getItemCount() = categories.size

    override fun setData(list: List<Category>) {
        categories = list
    }

    init {
        onBind(object : Bind<Category> {
            override fun onBinding(item: Category, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ReorderableView_Img.apply {
                        setBackgroundColor(context.getColor(item.color!!))
                        setImageDrawable(ContextCompat.getDrawable(context, item.icon!!))
                    }

                    list_ReorderableView_Name.text = item.name
                    list_ReorderableView_Info.isVisible = false

                    list_ReorderableView_Orderable.isVisible = false

                    setOnClickListener { callback.onItemClick(item.id!!) }
                }
            }
        })
    }
}