package com.umbrella.budgetapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPayments
import com.umbrella.budgetapp.enums.PayType
import com.umbrella.budgetapp.extensions.autoNotify
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_planned_payments.view.*
import kotlin.properties.Delegates

class PlannedPaymentsAdapter(val callBack: CallBack) : BaseAdapter<ExtendedPayments>() {

    var plannedPayments: List<ExtendedPayments> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.plannedPayment.id == n.plannedPayment.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_planned_payments))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(plannedPayments[position])
    }

    override fun getItemCount() = plannedPayments.size

    override fun setData(list: List<ExtendedPayments>) {
        plannedPayments = list
    }

    init {
        onBind(object : Bind<ExtendedPayments> {
            override fun onBinding(item: ExtendedPayments, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_PlannedPayments_Img.apply {
                        setBackgroundColor(context.getColor(item.category.color!!))
                        setImageDrawable(ContextCompat.getDrawable(context, item.category.icon!!))
                    }

                    list_PlannedPayments_Title.text = item.plannedPayment.name
                    list_PlannedPayments_Category.text = item.category.name

                    list_PlannedPayments_Information.text.apply {
                        item.plannedPayment.note

                        if (!item.plannedPayment.payee.isNullOrEmpty()) {
                            "\n" + item.plannedPayment.payee
                        }
                    }

                    list_PlannedPayments_Amount.apply {
                        currencyText(Memory.lastUsedCountry.symbol, item.plannedPayment.amount!!)

                        if (item.plannedPayment.type == PayType.EXPENSE) {
                            text = context.getString(R.string.negate, text)
                            setTextColor(context.getColor(R.color.negativeColor))
                        } else {
                            setTextColor(context.getColor(R.color.positiveColor))
                        }
                    }

                    // TODO: 30/08/2020 Find out how to display
                    list_PlannedPayments_Date.text = ""
                    list_PlannedPayments_Stamp.text = ""

                    setOnClickListener { callBack.onItemClick(item.plannedPayment.id!!) }
                }
            }
        })
    }
}