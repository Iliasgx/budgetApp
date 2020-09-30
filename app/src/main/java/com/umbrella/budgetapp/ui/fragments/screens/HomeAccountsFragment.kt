package com.umbrella.budgetapp.ui.fragments.screens

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.RecordsAdapter
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.submodels.HomeViewModel
import com.umbrella.budgetapp.databinding.FragmentHomeAccountsBinding
import com.umbrella.budgetapp.extensions.*
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.dialogs.FilterDialog.Filter
import com.umbrella.budgetapp.ui.dialogs.FilterDialog.Filter.FilterOption
import com.umbrella.budgetapp.ui.fragments.contentholders.HomeFragmentDirections
import com.umbrella.budgetapp.ui.fragments.contentholders.StatisticsFragment
import kotlinx.android.synthetic.main.list_home_account.view.*
import java.math.BigDecimal
import kotlin.properties.Delegates

class HomeAccountsFragment : ExtendedFragment(R.layout.fragment_home_accounts) {
    private val binding by viewBinding(FragmentHomeAccountsBinding::bind)

    val model by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })

    private val me = HomeFragmentDirections

    private var balanceValueToday = BigDecimal.ZERO
    private var cashflowCurrent = BigDecimal.ZERO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activateCashFlowFilter()
        activateRecordsFilter()

        binding.homeCardCashflowPeriod.text = resources.getStringArray(R.array.filterPeriods)[Memory.homeCashFlowFilter]
        binding.homeCardRecordsPeriod.text = resources.getStringArray(R.array.filterPeriods)[Memory.homeRecordsFilter]

        setListeners()
        setAdapters()
    }

    private fun activateCashFlowFilter() {
        val filterCashflow = Filter(FilterOption.getFilter(Memory.homeCashFlowFilter))

        model.cashflowFilterPeriod.postValue(filterCashflow.current().toResultPair())
        model.cashflowPreviousFilterPeriod.postValue(filterCashflow.previous().toResultPair())
    }

    private fun activateRecordsFilter() {
        val filterRecords = Filter(FilterOption.getFilter(Memory.homeRecordsFilter))
        model.recordsFilterPeriod.postValue(filterRecords.current().toResultPair())
    }

    private fun setListeners() {
        with(binding) {
            homeRecordsButton.setOnClickListener { navigate(me.homeAccountsToRecords()) }
            homeCardAccountsAll.setOnClickListener { navigate(me.homeAccountsToAccounts()) }

            homeCardBalanceMore.setOnClickListener { navigate(me.homeAccountsToStatistics(tab = StatisticsFragment.BALANCE)) }
            homeCardCashflowMore.setOnClickListener { navigate(me.homeAccountsToStatistics(tab = StatisticsFragment.CASHFLOW)) }
            homeCardRecordsMore.setOnClickListener { navigate(me.homeAccountsToRecords()) }

            homeCardCashflowFilter.setOnClickListener {
                navigate(me.homeToFilterDialog(0))

                getNavigationResult<FilterOption>(R.id.home, "filter@0") { result ->
                    Memory.homeCashFlowFilter = result.ordinal
                    binding.homeCardCashflowPeriod.text = resources.getStringArray(R.array.filterPeriods)[result.ordinal]
                    activateCashFlowFilter()
                }
            }
            homeCardRecordsFilter.setOnClickListener {
                navigate(me.homeToFilterDialog(1))

                getNavigationResult<FilterOption>(R.id.home, "filter@1") { result ->
                    Memory.homeRecordsFilter = result.ordinal
                    binding.homeCardRecordsPeriod.text = resources.getStringArray(R.array.filterPeriods)[result.ordinal]
                    activateRecordsFilter()
                }
            }
        }
    }

    private fun setAdapters() {
        val accountsAdapter = HomeAccountsAdapter(object : HomeAccountsAdapter.CallBack {
            override fun onItemSelection(itemId: Long, selected: Boolean, layout: View) {

                val result = model.selectedAccounts.value?.toMutableList() ?: mutableListOf()

                // Always at least 1 account selected (default first)
                if (!selected || result.size > 1) {
                    if (selected) result.remove(itemId) else result.add(itemId)

                    layout.alpha = if (selected) 0.9f else 1f

                    model.selectedAccounts.postValue(result)
                }
            }
        })

        binding.homeCardAccountsAccounts.adapter = accountsAdapter
        binding.homeCardAccountsAccounts.setHasFixedSize(false)

        model.accounts.observe(viewLifecycleOwner, Observer {
            // Makes sure that the first account is selected when data is found.
            // TODO: 24/09/2020 Save selected items in preferences.
            if (it.isNotEmpty()) model.selectedAccounts.postValue(listOf(it.first().account.id!!))

            accountsAdapter.setData(it)
        })

        model.balanceToday.observe(viewLifecycleOwner, Observer {
            balanceValueToday = it
            binding.homeCardBalanceAmount.currencyText(Memory.lastUsedCountry.symbol, it)
        })

        model.balancePrevious.observe(viewLifecycleOwner, Observer {
            val percentage = calculateBgDiff(balanceValueToday, it)

            binding.homeCardBalancePrevAmount.apply {
                text = getString(R.string.percentage, percentage.toEngineeringString())
                setTextColor(resources.getColor(
                        if (percentage.signum() != -1) { R.color.positiveColor } else { R.color.negativeColor },
                        context?.theme))
            }
        })

        model.cashflowCurrent.observe(viewLifecycleOwner, Observer {
            val total = it.first.add(it.second)

            cashflowCurrent = total

            binding.homeCardCashflowProgressbarIncome.apply {
                max = total.toInt()
                progress = it.first.toInt()
                value = it.first
            }

            binding.homeCardCashflowProgressbarExpenses.apply {
                max = total.toInt()
                progress = it.second.toInt()
                value = it.second
            }

            binding.homeCardCashflowAmount.currencyText(Memory.lastUsedCountry.symbol, total)
        })

        model.cashflowPrevious.observe(viewLifecycleOwner, Observer {
            val percentage = calculateBgDiff(cashflowCurrent, it)

            binding.homeCardCashflowPrevAmount.apply {
                text = getString(R.string.percentage, percentage.toEngineeringString())
                setTextColor(resources.getColor(
                        if (percentage.signum() != -1) { R.color.positiveColor } else { R.color.negativeColor },
                        context?.theme))
            }
        })

        val adapter = RecordsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                navigate(me.homeAccountsToUpdateRecordDetails(itemId))
            }
        })

        binding.homeCardRecordsList.fix(adapter)
        binding.homeCardRecordsList.setHasFixedSize(false)

        model.topRecords.observe(viewLifecycleOwner, Observer { adapter.setData(it) })
    }

    private fun navigate(direction: NavDirections) = findNavController().navigate(direction)

    private class HomeAccountsAdapter(val callback: CallBack) : BaseAdapter<ExtendedAccount>() {

        var accounts: List<ExtendedAccount> by Delegates.observable(emptyList()) {
            _, oldList, newList -> autoNotify(oldList, newList) { o, n -> o.account.id == n.account.id }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return BaseViewHolder(parent.inflate(R.layout.list_home_account))
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) { holder.bind(accounts[position]) }

        override fun getItemCount() = accounts.size

        override fun setData(list: List<ExtendedAccount>) { accounts = list }

        interface CallBack {
            fun onItemSelection(itemId: Long, selected: Boolean, layout: View)
        }

        init {
            onBind(object : Bind<ExtendedAccount> {
                override fun onBinding(item: ExtendedAccount, itemView: View, adapterPosition: Int) {
                    with(itemView) {
                        listHomeAccounts_name.text = item.account.name
                        listHomeAccounts_value.currencyText(DefaultCountries().getCountryById(item.countryRef).symbol, item.account.currentValue!!)

                        listHomeAccounts_layout.backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[item.account.color!!])
                        listHomeAccounts_layout.alpha = 0.8f

                        setOnClickListener {
                            val selected = (listHomeAccounts_layout.alpha == 1f)

                            listHomeAccounts_layout.alpha = if (selected) 0.8f else 1f

                            callback.onItemSelection(item.account.id!!, selected, listHomeAccounts_layout)
                        }
                    }
                }
            })
        }
    }
}