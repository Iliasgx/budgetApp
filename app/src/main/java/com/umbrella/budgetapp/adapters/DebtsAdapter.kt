package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_debts.view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
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
        holder.itemView.id = debts[position].debt.id!!.toInt()
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
                fun bind(item: ExtendedDebt) {
                    with(itemView) {
                        list_Debts_Title.text =
                                if (type == DebtType.BORROWED) {
                                    if (item.debt.name.isNullOrEmpty()) resources.getString(R.string.title_add_debt_borrowed) else resources.getString(R.string.title_add_debt_borrowed_extend, item.debt.name)
                                } else {
                                    if (item.debt.name.isNullOrEmpty()) resources.getString(R.string.title_add_debt_lent) else resources.getString(R.string.title_add_debt_lent_extend, item.debt.name)
                                }

                        list_Debts_Img.setImageResource(item.category?.icon!!)
                        list_Debts_Img.setBackgroundColor(item.category.color!!)
                        list_Debts_Name.text = item.debt.name
                        list_Debts_Information.text = item.debt.description
                        list_Debts_Amount.text = String.format("${Memory.lastUsedCountry.symbol} ${NumberFormat.getCurrencyInstance().format(item.debt.amount)}")
                        list_Debts_Date.text = SimpleDateFormat("DD/MM/YYYY", Locale.getDefault()).format(Date(item.debt.timestamp!!))

                        list_debts_create_record.setOnClickListener { callBack.onCreateRecord(debts[adapterPosition].debt) }
                    }

                    itemView.setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) callBack.onItemClick(debts[adapterPosition].debt.id!!)
                    }
                }
            }
        })
    }
}
