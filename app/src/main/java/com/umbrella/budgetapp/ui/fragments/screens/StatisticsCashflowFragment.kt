package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.submodels.StatisticsViewModel
import com.umbrella.budgetapp.databinding.FragmentStatisticsCashflowBinding
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class StatisticsCashflowFragment : ExtendedFragment(R.layout.fragment_statistics_cashflow) {
    private val binding by viewBinding(FragmentStatisticsCashflowBinding::bind)

    private val model by viewModels<StatisticsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
    }

    private fun setAdapters() {
        model.cashflowTotal.observe(viewLifecycleOwner, Observer {
            binding.statisticsCardCashflowProgressbarIncome.apply {
                max = it.first.add(it.second).toInt()
                value = it.first
                progress = it.first.toInt()
            }

            binding.statisticsCardCashflowProgressbarExpenses.apply {
                max = it.first.add(it.second).toInt()
                value = it.second
                progress = it.second.toInt()
            }

            binding.statisticsCardCashflowAmount.currencyText(DefaultCountries().getCountryByPosition(0).symbol, it.first.add(it.second))
        })

        model.cashFlowTable.observe(viewLifecycleOwner, Observer { map ->
            val symbol = Memory.lastUsedCountry.symbol

            with (binding) {
                map.getValue("count").let {
                    statisticsCardCashflowTableI1.text = it.first.toInt().toString()
                    statisticsCardCashflowTableE1.text = it.second.toInt().toString()
                }

                map.getValue("avgDay").let {
                    statisticsCardCashflowTableI2.currencyText(symbol, it.first)
                    statisticsCardCashflowTableE2.currencyText(symbol, it.second)
                }

                map.getValue("avdRecord").let {
                    statisticsCardCashflowTableI3.currencyText(symbol, it.first)
                    statisticsCardCashflowTableE3.currencyText(symbol, it.second)
                }

                map.getValue("total").let {
                    statisticsCardCashflowTableI4.currencyText(symbol, it.first)
                    statisticsCardCashflowTableE4.currencyText(symbol, it.second)
                }
            }
        })
    }
}