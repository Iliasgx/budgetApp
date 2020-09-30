package com.umbrella.budgetapp.ui.fragments.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.viewmodels.submodels.StatisticsViewModel
import com.umbrella.budgetapp.databinding.FragmentStatisticsBalanceBinding
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.inflate
import com.umbrella.budgetapp.extensions.orElse
import com.umbrella.budgetapp.extensions.sumByBigDecimal
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.android.synthetic.main.list_balance_currencies.view.*
import kotlinx.android.synthetic.main.list_stats_prgb.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class StatisticsBalanceFragment : ExtendedFragment(R.layout.fragment_statistics_balance) {
    private val binding by viewBinding(FragmentStatisticsBalanceBinding::bind)

    private val model by viewModels<StatisticsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
    }

    private fun setAdapters() {
        val statisticsBalanceAccountAdapter = StatisticsBalanceAccountsAdapter()
        val statisticsBalanceCurrencyAdapter = StatisticsBalanceCurrenciesAdapter()

        binding.statisticsCardBalanceRecyclerView.apply {
            adapter = statisticsBalanceAccountAdapter
            setHasFixedSize(false)
        }

        model.balanceAccounts.observe(viewLifecycleOwner, Observer {
            statisticsBalanceAccountAdapter.setData(it)
            binding.statisticsCardBalanceAmount.currencyText(Memory.lastUsedCountry.symbol, it.sumByBigDecimal { account -> account.currentValue!! })
        })

        binding.statisticsCardBalanceCurrenciesRecyclerView.apply {
            adapter = statisticsBalanceCurrencyAdapter
            setHasFixedSize(false)
        }

        model.balanceCurrencies.observe(viewLifecycleOwner, Observer {
            statisticsBalanceCurrencyAdapter.setData(it)
            binding.statisticsCardBalanceCurrenciesProgressbar.apply {
                max = it.sumByBigDecimal { trip -> trip.third }.toInt()
                progress = it.drop(1).sumByBigDecimal { trip -> trip.third }.toInt()
            }
        })

    }

    private class StatisticsBalanceAccountsAdapter : BaseAdapter<Account>() {

        private var accounts = listOf<Account>()

        private var total: BigDecimal = BigDecimal.ONE

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(parent.inflate(R.layout.list_stats_prgb))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) { holder.bind(accounts[position]) }

        override fun getItemCount() = accounts.size

        override fun setData(list: List<Account>) {
            total = list.sumByBigDecimal { account -> account.currentValue!! }
            accounts = list
            notifyDataSetChanged()
        }

        init {
            onBind(object : Bind<Account> {
                override fun onBinding(item: Account, itemView: View, adapterPosition: Int) {
                    itemView.list_stats_prgb_bar.apply {
                        title = item.name
                        max = total.toInt()
                        value = item.currentValue
                        progress = item.currentValue!!.divide(total, 0, RoundingMode.UP).toInt()
                        progressBarColor = item.color!!
                    }
                }
            })
        }
    }

    private class StatisticsBalanceCurrenciesAdapter : BaseAdapter<Triple<String, String, BigDecimal>>() {

        private var currencies = listOf<Triple<String, String, BigDecimal>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(parent.inflate(R.layout.list_balance_currencies))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) { holder.bind(currencies[position]) }

        override fun getItemCount() = currencies.size

        override fun setData(list: List<Triple<String, String, BigDecimal>>) {
            if (currencies == list) return
            currencies = list
            notifyDataSetChanged()
        }

        init {
            onBind(object : Bind<Triple<String, String, BigDecimal>> {
                override fun onBinding(item: Triple<String, String, BigDecimal>, itemView: View, adapterPosition: Int) {
                    with(itemView) {
                        val colors = resources.getIntArray(R.array.colors)
                        list_BalanceCurrencies_Color.backgroundTintList = ColorStateList.valueOf(colors[adapterPosition].orElse(adapterPosition - colors.size))
                        list_BalanceCurrencies_Name.text = item.first
                        list_BalanceCurrencies_Amount.currencyText(item.second, item.third)
                    }
                }
            })
        }
    }
}