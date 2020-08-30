package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.ShoppingListItemsAdapter
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class ShoppingListItemsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: ShoppingListItemsAdapter

    private lateinit var shoppingList: ShoppingList

    val model by viewModels<ShoppingListViewModel>()

    init {
        val args : ShoppingListItemsFragmentArgs by navArgs()

        model.getShoppingListById(args.shoppingListId).observe(viewLifecycleOwner, Observer {
            shoppingList = it

            if (!it.items.isNullOrEmpty()) {
                adapter.setData(it.items!!.toList())
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = ShoppingListItemsAdapter(object : ShoppingListItemsAdapter.CallBack {
            override fun onItemClick(position: Int) {
                // TODO: 30/08/2020 Find use
            }

            override fun onItemCheck(position: Int, checked: Boolean) {
                shoppingList.items?.get(position)?.checked = checked
                model.updateShoppingList(shoppingList)

            }

            override fun updateNumber(position: Int, number: Int) {
                shoppingList.items?.get(position)?.number = number
                model.updateShoppingList(shoppingList)
            }
        })

        binding.fragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
            setHasFixedSize(true)
        }
    }
}