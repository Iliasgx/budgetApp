package com.umbrella.budgetapp.ui.fragments.contentholders

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.ContentDebtsBinding
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.screens.DebtListFragment

class DebtsFragment: ExtendedFragment(R.layout.content_debts) {
    private val binding by viewBinding(ContentDebtsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()

        binding.debtsFABBorrowed.setOnClickListener { findNavController().navigate(DebtsFragmentDirections.debtsToUpdateDebt(debtType = DebtType.BORROWED.ordinal)) }
        binding.debtsFABLent.setOnClickListener { findNavController().navigate(DebtsFragmentDirections.debtsToUpdateDebt(debtType = DebtType.LENT.ordinal)) }
    }

    private fun setUpAdapter() {
        val mAdapter = HomeViewPagerAdapter(this)
        with(binding.debtsViewPager) {
            adapter = mAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        TabLayoutMediator(binding.debtsTabLayout, binding.debtsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.debts_Tab1)
                else -> getString(R.string.debts_Tab2)
            }
        }.attach()
    }

    class HomeViewPagerAdapter(fragment: ExtendedFragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int) = DebtListFragment(DebtType.values()[position])

        override fun getItemCount() = 2
    }
}