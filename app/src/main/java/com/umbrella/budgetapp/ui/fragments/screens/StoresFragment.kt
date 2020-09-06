package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.adapters.StoresAdapter
import com.umbrella.budgetapp.database.viewmodels.StoreViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class StoresFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter : StoresAdapter

    private val model by viewModels<StoreViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllStores().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.setOnClickListener {
            findNavController().navigate(StoresFragmentDirections.storesToUpdateStore())
        }
    }

    private fun setUpRecyclerView() {
        adapter = StoresAdapter(object: CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(StoresFragmentDirections.storesToUpdateStore(itemId))
            }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }
}