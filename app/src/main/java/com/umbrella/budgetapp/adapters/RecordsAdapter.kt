package com.umbrella.budgetapp.adapters

 import android.text.SpannableStringBuilder
 import android.view.View
 import android.view.ViewGroup
 import androidx.core.content.ContextCompat
 import androidx.core.text.italic
 import com.umbrella.budgetapp.R
 import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
 import com.umbrella.budgetapp.database.defaults.DefaultCountries
 import com.umbrella.budgetapp.extensions.DateTimeFormatter
 import com.umbrella.budgetapp.extensions.autoNotify
 import com.umbrella.budgetapp.extensions.currencyText
 import com.umbrella.budgetapp.extensions.inflate
 import kotlinx.android.synthetic.main.list_records.view.*
 import kotlin.properties.Delegates

class RecordsAdapter(val callBack: CallBack) : BaseAdapter<ExtendedRecord>() {

    var records: List<ExtendedRecord> by Delegates.observable(emptyList()) {
        _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.record.id == n.record.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_records))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount() = records.size

    override fun setData(list: List<ExtendedRecord>) {
        records = list
    }

    init {
        onBind(object : Bind<ExtendedRecord> {
            override fun onBinding(item: ExtendedRecord, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    list_Records_Img.apply {
                        setBackgroundColor(context.getColor(item.category.color!!))
                        setImageDrawable(ContextCompat.getDrawable(context, item.category.icon!!))
                    }

                    list_Records_Category.text = item.category.name
                    list_Records_Account.text = item.accountName

                    list_Records_Amount.apply {
                        currencyText(DefaultCountries().getCountryById(item.countryRef).symbol, item.record.amount!!)

                        if (item.record.type == 1) { // Expense
                            text = context.getString(R.string.negate, text)
                            setTextColor(context.getColor(R.color.negativeColor))
                        } else {
                            setTextColor(context.getColor(R.color.positiveColor))
                        }

                    }

                    list_Records_Date.text = DateTimeFormatter().dateFormat(item.record.timestamp!!)

                    list_Records_Information.text = SpannableStringBuilder().apply {

                        // Add description text to spannable if it has a value.
                        if (!item.record.description.isNullOrBlank()) {
                            append(item.record.description)
                        }

                        // Add payee text to spannable if it has a value.
                        if (!item.record.payee.isNullOrBlank()) {
                            // If it already contains the description, add a new line, otherwise not needed.
                            if (isNotEmpty()) appendln()

                            //Add payee text value as "payee"
                            italic { append("\"", item.record.payee, "\"") }
                        }
                    }

                    setOnClickListener { callBack.onItemClick(item.record.id!!) }
                }
            }
        })
    }
}