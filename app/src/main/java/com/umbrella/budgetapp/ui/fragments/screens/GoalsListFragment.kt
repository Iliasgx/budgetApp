package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.GoalsAdapter
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.contentholders.GoalsFragmentDirections

class GoalsListFragment(private val status: GoalStatus) : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: GoalsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 09/08/2020 Get data with status
        //status

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = GoalsAdapter(status, object : GoalsAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                view?.findNavController()?.navigate(GoalsFragmentDirections.goalsToGoalDetails(itemId))
            }
        })
        binding.fragmentRecyclerView.adapter = adapter
    }
}