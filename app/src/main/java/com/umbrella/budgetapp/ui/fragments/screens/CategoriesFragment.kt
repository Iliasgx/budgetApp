package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.adapters.CategoriesAdapter
import com.umbrella.budgetapp.database.viewmodels.CategoryViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class CategoriesFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private val model by viewModels<CategoryViewModel>()

    private lateinit var adapter : CategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllCategories().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.apply {
            isVisible = true
            setOnClickListener {
                findNavController().navigate(CategoriesFragmentDirections.categoriesToUpdateCategory())
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = CategoriesAdapter(object : CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(CategoriesFragmentDirections.categoriesToUpdateCategory(itemId))
            }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }
}