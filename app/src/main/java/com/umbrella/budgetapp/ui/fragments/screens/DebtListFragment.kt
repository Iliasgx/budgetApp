package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.DebtsAdapter
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.viewmodels.DebtViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.contentholders.DebtsFragmentDirections

class DebtListFragment(private val type: DebtType)  : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    val model by viewModels<DebtViewModel>()

    private lateinit var adapter : DebtsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllDebts(type.ordinal).observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = DebtsAdapter(type, object : DebtsAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(DebtsFragmentDirections.debtsToUpdateDebt(itemId))
            }

            override fun onCreateRecord(item: Debt) { createRecord(item) }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }

    private fun createRecord(debt: Debt) {
        findNavController().navigate(DebtListFragmentDirections.globalAddRecord(
                debt.accountRef!!,
                debt.categoryRef!!,
                debt.amount.toString(),
                debt.currencyRef!!,
                debt.description.orEmpty()
        ))

        getNavigationResult<Boolean>(R.id.debtListFragment, "record") { findNavController().navigateUp() }
    }
}