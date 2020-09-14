package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.collections.ShoppingListItem
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_shopping_lists.view.*
import kotlin.properties.Delegates

class ShoppingListAdapter(val callback: CallBack) : BaseAdapter<ShoppingList>() {

    var shoppingLists: List<ShoppingList> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_shopping_lists))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(shoppingLists[position])
    }

    override fun getItemCount() = shoppingLists.size

    override fun setData(list: List<ShoppingList>) {
        shoppingLists = list
    }

    interface CallBack : BaseAdapter.CallBack {
        fun onAddItemClick(shoppingList: ShoppingList)
    }

    init {
        onBind(object : Bind<ShoppingList> {
            override fun onBinding(item: ShoppingList, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ShoppingList_Name.text = item.name
                    list_ShoppingList_NbItems.text = context.getString(R.string.shoppingLists_nrOfItems, item.items?.size ?: 0)
                    list_ShoppingList_Amount.currencyText(Memory.lastUsedCountry.symbol, sumAmount(item.items))

                    list_ShoppingList_Add.setOnClickListener { callback.onAddItemClick(item) }
                    this.setOnClickListener { callback.onItemClick(item.id!!) }
                }
            }

            private fun sumAmount(itemList: List<ShoppingListItem>?) = itemList
                    .orEmpty()
                    .sumByDouble { item -> item.amount.toDouble() }
                    .toBigDecimal()
        })
    }
}