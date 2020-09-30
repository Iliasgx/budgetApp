package com.umbrella.budgetapp.ui.fragments.contentholders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.viewmodels.submodels.StatisticsViewModel
import com.umbrella.budgetapp.databinding.ContentStatisticsBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.screens.StatisticsBalanceFragment
import com.umbrella.budgetapp.ui.fragments.screens.StatisticsCashflowFragment
import com.umbrella.budgetapp.ui.fragments.screens.StatisticsReportsFragment
import com.umbrella.budgetapp.ui.fragments.screens.StatisticsSpendingFragment

class StatisticsFragment: ExtendedFragment(R.layout.content_statistics) {
    private val binding by viewBinding(ContentStatisticsBinding::bind)

    private val model by viewModels<StatisticsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()
    }

    private fun setUpAdapter() {
        binding.statisticsViewPager.adapter = StatisticsStateAdapter(this)

        TabLayoutMediator(binding.statisticsTabLayout, binding.statisticsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.statistics_Tab1)
                1 -> getString(R.string.statistics_Tab2)
                2 -> getString(R.string.statistics_Tab3)
                else -> getString(R.string.statistics_Tab4)
            }
        }.attach()
    }

    class StatisticsStateAdapter(fragment: ExtendedFragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): ExtendedFragment {
            return when (position) {
                0 -> StatisticsBalanceFragment()
                1 -> StatisticsCashflowFragment()
                2 -> StatisticsSpendingFragment()
                else -> StatisticsReportsFragment()
            }
        }

        override fun getItemCount() = 4
    }

    companion object Tab {
        const val BALANCE = 0
        const val CASHFLOW = 1
        const val SPENDING = 2
        const val REPORTS = 3
    }
}