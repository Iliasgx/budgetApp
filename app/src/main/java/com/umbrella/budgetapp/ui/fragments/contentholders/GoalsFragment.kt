package com.umbrella.budgetapp.ui.fragments.contentholders

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.ContentGoalsBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.screens.GoalsListFragment

class GoalsFragment: ExtendedFragment(R.layout.content_goals) {
    private val binding by viewBinding(ContentGoalsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()

        binding.goalsFAB.setOnMenuButtonClickListener { findNavController().navigate(GoalsFragmentDirections.goalsToGoalSelect()) }
    }

    private fun setUpAdapter() {
        val adapter = GoalViewPagerAdapter(this)
        binding.goalsViewPager.adapter = adapter

        TabLayoutMediator(binding.goalsTabLayout, binding.goalsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.goals_Tab1)
                1 -> getString(R.string.goals_Tab2)
                else -> getString(R.string.goals_Tab3)
            }
        }.attach()
    }

    inner class GoalViewPagerAdapter(fragment: ExtendedFragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int) = GoalsListFragment(GoalStatus.values()[position])

        override fun getItemCount() = 3
    }
}