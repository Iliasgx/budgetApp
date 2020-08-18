package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_debts.view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class DebtsAdapter(private val type: DebtType, val callBack : CallBack) : RecyclerView.Adapter<DebtsAdapter.DebtViewHolder>() {

    var debts: List<ExtendedDebt> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.debt.id == n.debt.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        return DebtViewHolder(parent.inflate(R.layout.list_debts))
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) = holder.bind(debts[position])

    override fun getItemCount() = debts.size

    interface CallBack {
        fun onItemClick(itemId: Long)
        fun onCreateRecord(itemId: Long)
    }

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ExtendedDebt) {
            with(itemView) {
                list_Debts_Title.text =
                    if (item.debt.debtType == DebtType.BORROWED) {
                        if (item.debt.name.isNullOrEmpty()) resources.getString(R.string.title_add_debt_borrowed) else resources.getString(R.string.title_add_debt_borrowed_extend, item.debt.name)
                    } else {
                        if (item.debt.name.isNullOrEmpty()) resources.getString(R.string.title_add_debt_lent) else resources.getString(R.string.title_add_debt_lent_extend, item.debt.name)
                    }

                list_Debts_Img.setImageResource(item.category.icon!!)
                list_Debts_Img.setBackgroundColor(item.category.color!!)
                list_Debts_Name.text = item.debt.name
                list_Debts_Information.text = item.debt.description
                list_Debts_Amount.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(item.debt.amount)}")
                list_Debts_Date.text = SimpleDateFormat("DD/MM/YYYY", Locale.getDefault()).format(Date(item.debt.timestamp!!))

                list_debts_create_record.setOnClickListener { callBack.onCreateRecord(debts[adapterPosition].debt.id!!) }
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(debts[adapterPosition].debt.id!!)
            }
        }
    }
}