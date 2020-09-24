package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.PlannedPaymentsAdapter
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.viewmodels.submodels.HomeViewModel
import com.umbrella.budgetapp.databinding.FragmentHomeInformationBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.contentholders.HomeFragmentDirections

class HomeInformationFragment : ExtendedFragment(R.layout.fragment_home_information) {
    private val binding by viewBinding(FragmentHomeInformationBinding::bind)

    private val model by viewModels<HomeViewModel>(ownerProducer = { requireParentFragment() })

    private val me = HomeFragmentDirections

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setAdapters()
    }

    private fun setListeners() {
        with(binding) {
            homeMenuScrollItem1.setOnClickListener { navigate(me.homeInformationToRecords()) }
            homeMenuScrollItem2.setOnClickListener { navigate(me.homeInformationToShoppingLists()) }
            homeMenuScrollItem3.setOnClickListener { navigate(me.homeInformationToPlannedPayments()) }
            homeMenuScrollItem4.setOnClickListener { navigate(me.homeInformationToDebts()) }
            homeMenuScrollItem5.setOnClickListener { navigate(me.homeInformationToGoals()) }

            homeCardPlannedPaymentsMore.setOnClickListener { navigate(me.homeInformationToPlannedPayments()) }

            homeCardDebtsMore.setOnClickListener {
                //TODO: Open BottomSheet for lent/borrowed and then continue to screen.
                //navigate(me.homeInformationToUpdateDebt())
            }
        }
    }

    private fun setAdapters() {
        val plannedPaymentAdapter = PlannedPaymentsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                navigate(me.homeInformationToUpdatePlannedPayment(itemId))
            }
        })
        binding.homeCardPlannedPaymentsUpcoming.fix(plannedPaymentAdapter)
        binding.homeCardPlannedPaymentsUpcoming.setHasFixedSize(false)

        model.plannedPayments.observe(viewLifecycleOwner, Observer { plannedPaymentAdapter.setData(it) })

        model.debts.observe(viewLifecycleOwner, Observer {
            val total = it[0].total.add(it[1].total)

            // FIXME: 21/09/2020 Show progressColor not working

            binding.homeCardDebtsProgressbarLent.apply {
                max = total.toInt()
                progress = it[0].total.toInt()
                value = "${Memory.lastUsedCountry.symbol} ${String.format("%.2f", it[0].total)}"
            }

            binding.homeCardDebtsProgressbarBorrowed.apply {
                max = total.toInt()
                progress = it[1].total.toInt()
                value = "${Memory.lastUsedCountry.symbol} ${String.format("%.2f", it[1].total)}"
            }
        })

        // TODO: 20/09/2020 shopping lists
    }

    private fun navigate(direction: NavDirections) = findNavController().navigate(direction)
}