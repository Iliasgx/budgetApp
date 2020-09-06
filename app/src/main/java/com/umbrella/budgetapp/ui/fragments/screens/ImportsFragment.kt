package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.ImportAdapter
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.databinding.FragmentImportsBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class ImportsFragment : ExtendedFragment(R.layout.fragment_imports) {
    private val binding by viewBinding(FragmentImportsBinding::bind)

    val model by viewModels<AccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()

        binding.importsCardAppImport.setOnClickListener {
            // TODO-UPCOMING: Import all accounts (whole app)
        }

        binding.importsCardNewCreate.setOnClickListener {
            findNavController().navigate(ImportsFragmentDirections.importsToUpdateAccount())
        }
    }

    private fun setUpAdapter() {
        val adapter = ImportAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            }
        })

        binding.importsAccountRecyclerView.fix(adapter)

        model.getAccountBasics().observe(viewLifecycleOwner, Observer { adapter.setData(it) })
    }
}