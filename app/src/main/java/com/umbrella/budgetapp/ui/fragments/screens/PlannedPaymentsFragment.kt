package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.PlannedPaymentsAdapter
import com.umbrella.budgetapp.database.viewmodels.PlannedPaymentViewModel
import com.umbrella.budgetapp.databinding.FragmentPlannedPaymentsBinding
import com.umbrella.budgetapp.enums.PayType
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class PlannedPaymentsFragment : ExtendedFragment(R.layout.fragment_planned_payments) {
    private val binding by viewBinding(FragmentPlannedPaymentsBinding::bind)

    private lateinit var adapter: PlannedPaymentsAdapter

    val model by viewModels<PlannedPaymentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllPlannedPayments().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.plannedPaymentsFABIncome.setOnClickListener { findNavController().navigate(PlannedPaymentsFragmentDirections.plannedPaymentsToUpdatePlannedPayments(type = PayType.INCOME)) }
        binding.plannedPaymentsFABExpenses.setOnClickListener { findNavController().navigate(PlannedPaymentsFragmentDirections.plannedPaymentsToUpdatePlannedPayments(type = PayType.EXPENSE)) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView() {
        adapter = PlannedPaymentsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(PlannedPaymentsFragmentDirections.plannedPaymentsToUpdatePlannedPayments(itemId))
            }
        })

        binding.plannedPaymentsRecyclerView.fix(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_Sort) {
            // TODO: 06/09/2020 open sort dialog
        }
        return true
    }
}