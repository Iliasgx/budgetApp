package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_reorderable_view.view.*
import kotlin.properties.Delegates

class AccountsAdapter(val callback: CallBack) : BaseAdapter<Account>() {

    var accounts: List<Account> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_reorderable_view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount() = accounts.size

    override fun setData(list: List<Account>) {
        accounts = list
    }

    init {
        onBind(object : Bind<Account> {
            override fun onBinding(item: Account, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_ReorderableView_Img.apply {
                        setImageResource(R.drawable.account)
                        backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.color!!])
                    }

                    list_ReorderableView_Name.text = item.name

                    list_ReorderableView_Info.text = if (item.excludeStats!!) {
                        context.getString(R.string.account_excluded)
                    } else {
                        resources.getStringArray(R.array.accountType)[item.type!!]
                    }

                    setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClick(item.id!!)
                    }
                }
            }
        })
    }
}