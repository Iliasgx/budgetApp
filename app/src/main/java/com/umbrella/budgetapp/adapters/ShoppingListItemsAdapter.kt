package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView.BufferType.EDITABLE
import androidx.core.widget.doAfterTextChanged
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.ShoppingListItem
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_shoppinglist_items.view.*

class ShoppingListItemsAdapter(val callBack: CallBack) : BaseAdapter<ShoppingListItem>() {

    /*var items: List<ShoppingListItem> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.name == n.name }
    }*/

    var items: List<ShoppingListItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_shoppinglist_items))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    override fun setData(list: List<ShoppingListItem>) {
        if (list != items) {
            items = list
            notifyDataSetChanged()
        }
    }

    interface CallBack {
        fun onItemClick(position: Int)
        fun onItemCheck(position: Int, checked: Boolean)
        fun updateNumber(position: Int, number: Int)
    }

    init {
        onBind(object : Bind<ShoppingListItem> {
            override fun onBinding(item: ShoppingListItem, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ShoppingListItems_Item.apply {
                        isChecked = item.checked
                        text = item.name
                    }

                    list_ShoppingListItems_Number.setText(item.number.toString(), EDITABLE)
                    list_ShoppingListItems_Amount.currencyText(Memory.lastUsedCountry.symbol, item.amount)

                    list_ShoppingListItems_Item.setOnCheckedChangeListener { _, isChecked ->
                        callBack.onItemCheck(item.position, isChecked)
                    }

                    list_ShoppingListItems_Number.doAfterTextChanged { text ->
                        if (!text.isNullOrBlank()) {
                            callBack.updateNumber(item.position, Integer.valueOf(text.toString()))
                        }
                    }

                    setOnClickListener { callBack.onItemClick(item.position) }
                }
            }
        })
    }
}