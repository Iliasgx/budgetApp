package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.adapters.RecordsAdapter
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.databinding.FragmentRecordsBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class RecordsFragment : ExtendedFragment(R.layout.fragment_records) {
    private val binding by viewBinding(FragmentRecordsBinding::bind)

    private lateinit var adapter: RecordsAdapter

    val model by viewModels<RecordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllRecords().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = RecordsAdapter(object : BaseAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(RecordsFragmentDirections.recordsToUpdateRecordDetails(itemId))
            }
        })

        binding.fragmentRecordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
            setHasFixedSize(true)
        }
    }
}