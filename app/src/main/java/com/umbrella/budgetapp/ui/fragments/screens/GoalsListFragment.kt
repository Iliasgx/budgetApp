package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.GoalsAdapter
import com.umbrella.budgetapp.database.viewmodels.GoalViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.enums.GoalStatus
import com.umbrella.budgetapp.enums.GoalStatus.REACHED
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.contentholders.GoalsFragmentDirections

class GoalsListFragment(private val status: GoalStatus) : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: GoalsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model by viewModels<GoalViewModel>()

        when (status) {
            REACHED -> model.getAllGoalsReached().observe(viewLifecycleOwner, Observer { adapter.setData(it) })
            else -> model.getAllGoalsUnreached(status.ordinal).observe(viewLifecycleOwner, Observer { adapter.setData(it) })
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = GoalsAdapter(status, object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                view?.findNavController()?.navigate(GoalsFragmentDirections.goalsToGoalDetails(itemId))
            }
        })
        binding.fragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = adapter
        }
    }
}