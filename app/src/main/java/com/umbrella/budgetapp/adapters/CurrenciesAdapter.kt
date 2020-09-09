package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_reorderable_view.view.*
import kotlin.properties.Delegates

class CurrenciesAdapter(val callback: CallBack) : BaseAdapter<Currency>() {

    var currencies: List<Currency> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_reorderable_view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(currencies[position])
        holder.itemView.id = currencies[position].id!!.toInt()
    }

    override fun getItemCount() = currencies.size

    override fun setData(list: List<Currency>) {
        currencies = list
    }

    init {
        onBind(object: Bind<Currency> {
            override fun onBinding(item: Currency, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ReorderableView_Img.isVisible = false

                    val country = DefaultCountries().getCountryById(item.countryRef)

                    list_ReorderableView_Name.text = country.name
                    list_ReorderableView_Info.text = country.symbol

                    setOnClickListener { callback.onItemClick(item.id!!) }
                }
            }
        })
    }
}