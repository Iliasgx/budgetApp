package com.umbrella.budgetapp.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.extensions.inflate
import kotlinx.android.synthetic.main.list_basic.view.*
import kotlin.random.Random

/**
 * Class for creating an adapter in a DialogFragment RecyclerView.
 * Supported classes:
 *      - Account
 *      - TemplateAndCategory
 *      - Category
 *      - ExtendedCurrency
 *      - ExtendedStore
 */
class DataListAdapter<T>(val callBack: DataCallBack<T>) : BaseAdapter<T>() {

    var items: List<T> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.inflate(R.layout.list_basic))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    /**
     * Adapter not automatically updated because unknown T.
     */
    override fun setData(list: List<T>) {
        items = list
    }

    interface DataCallBack<T> {
        fun onItemSelected(item: T)
    }

    init {
        onBind(object : Bind<T> {
            override fun onBinding(item: T, itemView: View, adapterPosition: Int) {
                with(itemView) {
                    val arr = resources.getIntArray(R.array.colors)

                    fun setImage(resId: Int) = list_Basic_Img.setImageResource(resId)
                    fun setColor(color: Int) = ViewCompat.setBackgroundTintList(list_Basic_Img, ColorStateList.valueOf(arr[color]))
                    fun setName(name: String) { list_Basic_Name.text = name }

                    when (item) {
                        is Account -> {
                            setImage(R.drawable.account)
                            setColor(item.color!!)
                            setName(item.name!!)
                        }
                        is TemplateAndCategory -> {
                            setImage(item.category.icon!!)
                            setColor(item.category.color!!)
                            setName(item.template.name!!)
                        }
                        is Category -> {
                            setImage(item.icon!!)
                            setColor(item.color!!)
                            setName(item.name!!)
                        }
                        is Currency -> {
                            setImage(R.drawable.saving_account)
                            setColor(Random.nextInt(0, arr.size - 1))
                            setName(DefaultCountries().getCountryById(item.countryRef).name)
                        }
                        is ExtendedStore -> {
                            setImage(item.category?.icon!!)
                            setColor(item.category.color!!)
                            setName(item.store.name!!)
                        }
                        else -> throw RuntimeException("Binding failed. Class not supported for DataListAdapter.")
                    }
                    setOnClickListener { callBack.onItemSelected(item) }
                }
            }
        })
    }
}