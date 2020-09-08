package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.ShoppingListAdapter
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class ShoppingListsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: ShoppingListAdapter

    val model by viewModels<ShoppingListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentFloatingActionButton.isVisible = true

        model.getAllShoppingLists().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.setOnClickListener {
            findNavController().navigate(ShoppingListsFragmentDirections.shoppingListsToUpdateShoppingList())
        }
    }

    private fun setUpRecyclerView() {
        adapter = ShoppingListAdapter(object : ShoppingListAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(ShoppingListsFragmentDirections.shoppingListsToShoppingListItems(itemId))
            }

            override fun onAddItemClick(itemId: Long) {
                // TODO: Open Dialog for adding item directly
            }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }
}