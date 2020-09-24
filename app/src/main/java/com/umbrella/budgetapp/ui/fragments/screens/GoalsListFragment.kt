package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.adapters.GoalsAdapter
import com.umbrella.budgetapp.database.viewmodels.GoalViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.contentholders.GoalsFragmentDirections

class GoalsListFragment(private val status: GoalStatus) : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    val model by viewModels<GoalViewModel>()

    private lateinit var adapter: GoalsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        model.getAllGoals(status).observe(viewLifecycleOwner, Observer { adapter.setData(it) })

    }

    private fun setUpRecyclerView() {
        adapter = GoalsAdapter(status, object : CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(GoalsFragmentDirections.goalsToGoalDetails(itemId))
            }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }
}