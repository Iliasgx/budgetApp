package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.ShoppingListItemsAdapter
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class ShoppingListItemsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    val model by viewModels<ShoppingListViewModel>()

    val args : ShoppingListItemsFragmentArgs by navArgs()

    private lateinit var adapter: ShoppingListItemsAdapter

    private lateinit var shoppingList: ShoppingList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        model.getShoppingListById(args.shoppingListId).observe(viewLifecycleOwner, Observer {
            shoppingList = it

            setTitle(shoppingList.name!!)

            if (!it.items.isNullOrEmpty()) {
                adapter.setData(it.items!!.toList())
            }
        })

        binding.fragmentFloatingActionButton.setOnClickListener { addItem() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminder_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView() {
        adapter = ShoppingListItemsAdapter(object : ShoppingListItemsAdapter.CallBack {
            override fun onItemClick(position: Int) {
                // TODO: 30/08/2020 Open dialog again to change name and price
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

        binding.fragmentRecyclerView.fix(adapter)
    }

    private fun addItem() {
        // TODO: 06/09/2020 add new item to shopping list
    }

    private fun createRecord() {
        // TODO: 06/09/2020 Dialog record

        findNavController().navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuLayout_ReminderOptions_Reminder -> {
                // TODO: 06/09/2020 Reminder Dialog
                true
            }
            R.id.menuLayout_ReminderOptions_Rename -> {
                // TODO: 06/09/2020 Dialog to rename (title automatically updated)
                true
            }
            R.id.menuLayout_ReminderOptions_CreateRecord -> {
                createRecord()
                true
            }
            R.id.menuLayout_ReminderOptions_DeleteList -> {
                model.removeShoppingList(shoppingList)
                findNavController().navigateUp()
                true
            }
            else -> false
        }

    }
}