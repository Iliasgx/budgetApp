package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_stores.view.*
import kotlin.properties.Delegates

class StoresAdapter(val callback: CallBack) : BaseAdapter<ExtendedStore>() {

    var stores: List<ExtendedStore> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.store.id == n.store.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_stores))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(stores[position])
        holder.itemView.id = stores[position].store.id!!.toInt()
    }

    override fun getItemCount() = stores.size

    override fun setData(list: List<ExtendedStore>) {
        stores = list
    }

    init {
        onBind(object : Bind<ExtendedStore> {
            override fun onBinding(item: ExtendedStore, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_Stores_Img.apply {
                        setImageResource(item.category?.icon!!)
                        backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.category.color!!])
                    }

                    list_Stores_Name.text = item.store.name
                    list_Stores_Category.text = item.category?.name

                    list_Stores_Country.text = DefaultCountries().getCountryById(item.countryRef).name

                    setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClick(item.store.id!!)
                    }
                }
            }
        })
    }
}