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
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPayments
import com.umbrella.budgetapp.database.viewmodels.PlannedPaymentViewModel
import com.umbrella.budgetapp.databinding.FragmentPlannedPaymentsBinding
import com.umbrella.budgetapp.enums.PayType
import com.umbrella.budgetapp.enums.SortType
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class PlannedPaymentsFragment : ExtendedFragment(R.layout.fragment_planned_payments) {
    private val binding by viewBinding(FragmentPlannedPaymentsBinding::bind)

    private lateinit var adapter: PlannedPaymentsAdapter

    val model by viewModels<PlannedPaymentViewModel>()

    private var sortType = SortType.DEFAULT

    private var originalList = mutableListOf<ExtendedPayments>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 08/09/2020 get sortType of preferences

        setUpRecyclerView()

        model.getAllPlannedPayments().observe(viewLifecycleOwner, Observer {
            originalList = it.toMutableList()
            adaptSorting()
        })


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

        getNavigationResult<SortType>(R.id.plannedPayments, "type") { result ->
            this.sortType = result
            adaptSorting()
        }
    }

    private fun adaptSorting() {
        val tempList = originalList.toMutableList()

        with (tempList) {
            adapter.setData(when (sortType) {
                SortType.DEFAULT -> tempList
                SortType.AZ -> {
                    sortByDescending { it.plannedPayment.name }
                    this
                }
                SortType.ZA -> {
                    sortByDescending { it.plannedPayment.name }
                    reverse()
                    this
                }
                SortType.NEWEST -> {
                    // TODO: 08/09/2020 Function for getting the next payments first
                    this
                }
                SortType.OLDEST -> {
                    // TODO: 08/09/2020 Function for getting the furthest payments first
                    this
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_Sort) {
            findNavController().navigate(PlannedPaymentsFragmentDirections.plannedPaymentsToSortDialog(sortType))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}