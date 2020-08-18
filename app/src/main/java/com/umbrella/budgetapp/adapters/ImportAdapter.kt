package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_import.view.*

class ImportAdapter(val callBack: CallBack) : RecyclerView.Adapter<ImportAdapter.ImportHolder>() {

    private var accounts = emptyList<Account>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportHolder {
        return ImportHolder(parent.inflate(R.layout.list_import))
    }

    override fun onBindViewHolder(holder: ImportHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount() = accounts.size

    fun setData(accounts : List<Account>) {
        this.accounts = accounts
        notifyDataSetChanged()
    }

    interface CallBack {
        fun onItemClick(itemId: Long)
    }

    inner class ImportHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(account: Account) {
            with(itemView) {
                listImport_accountName.text = account.name

                listImport_import.setOnClickListener { callBack.onItemClick(accounts[adapterPosition].id!!) }
            }
        }
    }
}