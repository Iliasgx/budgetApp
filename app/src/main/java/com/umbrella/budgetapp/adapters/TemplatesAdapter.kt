package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_reorderable_view.view.*
import kotlin.properties.Delegates

class TemplatesAdapter(val callback: CallBack) : BaseAdapter<TemplateAndCategory>() {

    var templates: List<TemplateAndCategory> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.template.id == n.template.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_reorderable_view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(templates[position])
        holder.itemView.id = templates[position].template.id!!.toInt()
    }

    override fun getItemCount() = templates.size

    override fun setData(list: List<TemplateAndCategory>) {
        templates = list
    }

    init {
        onBind(object : Bind<TemplateAndCategory> {
            override fun onBinding(item: TemplateAndCategory, itemView: View, adapterPosition: Int) {
                with (itemView) {
                    list_ReorderableView_Img.apply {
                        setImageResource(item.category.icon!!)
                        backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.category.color!!])
                    }

                    list_ReorderableView_Name.text = item.template.name
                    list_ReorderableView_Info.text = item.category.name

                    setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClick(item.template.id!!)
                    }
                }
            }
        })
    }
}