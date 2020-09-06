package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedCurrency
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_reorderable_view.view.*
import kotlin.properties.Delegates

class CurrenciesAdapter(val callback: CallBack) : BaseAdapter<ExtendedCurrency>() {

    var currencies: List<ExtendedCurrency> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.currency.id == n.currency.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_reorderable_view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(currencies[position])
        holder.itemView.id = currencies[position].currency.id!!.toInt()
    }

    override fun getItemCount() = currencies.size

    override fun setData(list: List<ExtendedCurrency>) {
        currencies = list
    }

    init {
        onBind(object: Bind<ExtendedCurrency> {
            override fun onBinding(item: ExtendedCurrency, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ReorderableView_Img.isVisible = false

                    list_ReorderableView_Name.text = item.country?.name
                    list_ReorderableView_Info.text = item.country?.symbol

                    setOnClickListener { callback.onItemClick(item.currency.id!!) }
                }
            }
        })
    }
}