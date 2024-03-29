package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.RecordsAdapter
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.databinding.FragmentRecordsBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class RecordsFragment : ExtendedFragment(R.layout.fragment_records) {
    private val binding by viewBinding(FragmentRecordsBinding::bind)

    private lateinit var adapter: RecordsAdapter

    val model by viewModels<RecordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllRecords().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.recordsFAB.setOnClickListener {
            findNavController().navigate(RecordsFragmentDirections.recordsToUpdateRecordBasic())
        }

        // TODO: 06/09/2020 set filterPeriod text and total amount
    }

    private fun setUpRecyclerView() {
        adapter = RecordsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(RecordsFragmentDirections.recordsToUpdateRecordDetails(itemId))
            }
        })

        binding.fragmentRecordsRecyclerView.fix(adapter)
    }
}