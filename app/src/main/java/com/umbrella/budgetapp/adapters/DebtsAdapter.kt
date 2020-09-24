package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_debts.view.*
import kotlin.properties.Delegates

class DebtsAdapter(private val type : DebtType, val callBack: CallBack) : BaseAdapter<ExtendedDebt>() {

    var debts: List<ExtendedDebt> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.debt.id == n.debt.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_debts))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(debts[position])
    }

    override fun getItemCount() = debts.size

    override fun setData(list: List<ExtendedDebt>) {
        debts = list
    }

    interface CallBack : BaseAdapter.CallBack {
        fun onCreateRecord(item: Debt)
    }

    init {
        onBind(object : Bind<ExtendedDebt> {
            override fun onBinding(item: ExtendedDebt, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_Debts_Title.text = if (type == DebtType.BORROWED)  resources.getString(R.string.title_add_debt_borrowed_extend, item.debt.name) else resources.getString(R.string.title_add_debt_lent_extend, item.debt.name)

                    list_Debts_Img.apply {
                        setImageResource(item.category?.icon!!)
                        backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.category.color!!])
                    }

                    list_Debts_Name.text = item.debt.name
                    list_Debts_Information.text = item.debt.description
                    list_Debts_Amount.currencyText(Memory.lastUsedCountry.symbol, item.debt.amount!!)
                    list_Debts_Date.text = DateTimeFormatter().dateFormat(item.debt.timestamp!!, '/')

                    list_debts_create_record.setOnClickListener { callBack.onCreateRecord(debts[adapterPosition].debt) }
                }

                itemView.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(debts[adapterPosition].debt.id!!)
                }
            }
        })
    }
}
