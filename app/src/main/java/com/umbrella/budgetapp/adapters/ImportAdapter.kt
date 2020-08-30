package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_import.view.*

class ImportAdapter(val callBack: CallBack) : BaseAdapter<Account>() {

    private var accounts = emptyList<Account>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_import))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(accounts[position])
        holder.itemView.id = accounts[position].id!!.toInt()
    }

    override fun getItemCount() = accounts.size

    override fun setData(list : List<Account>) {
        accounts = list
        notifyDataSetChanged()
    }

    init {
        onBind(object : Bind<Account> {
            override fun onBinding(item: Account, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    listImport_accountName.text = item.name
                    listImport_import.setOnClickListener { callBack.onItemClick(accounts[adapterPosition].id!!) }
                }
            }
        })
    }
}