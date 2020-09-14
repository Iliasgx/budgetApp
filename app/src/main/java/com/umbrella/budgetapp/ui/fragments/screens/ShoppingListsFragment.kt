package com.umbrella.budgetapp.ui.fragments.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.ShoppingListAdapter
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.collections.ShoppingListItem
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import java.math.BigDecimal

class ShoppingListsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: ShoppingListAdapter

    val model by viewModels<ShoppingListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentFloatingActionButton.isVisible = true

        setUpRecyclerView()

        model.getAllShoppingLists().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        binding.fragmentFloatingActionButton.setOnClickListener {
            findNavController().navigate(ShoppingListsFragmentDirections.shoppingListsToUpdateShoppingList())
        }
    }

    private fun setUpRecyclerView() {
        adapter = ShoppingListAdapter(object : ShoppingListAdapter.CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(ShoppingListsFragmentDirections.shoppingListsToShoppingListItems(itemId))
            }

            override fun onAddItemClick(shoppingList: ShoppingList) { addItem(shoppingList) }
        })

        binding.fragmentRecyclerView.fix(adapter)
    }

    // Function for adding a new item directly
    private fun addItem(item: ShoppingList) {
        lateinit var dg: AlertDialog

        val viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_new_item, view as ViewGroup, false)

        val editTextName = viewInflated.findViewById<TextInputEditText>(R.id.dialog_newItem_name)
        val editTextPrice = viewInflated.findViewById<TextInputEditText>(R.id.dialog_newItem_price)

        // Inner function for checking the values.
        fun innerCheck(action: Int) : Boolean {
            if (action == EditorInfo.IME_ACTION_DONE) {

                val mName = editTextName.text?.trim()

                // Name is required for the Items, price not.
                if (mName.isNullOrBlank()) {
                    editTextName.error = getString(R.string.dialog_NewItem_error_name)
                } else {
                    val mPrice = if (editTextPrice.text.isNullOrBlank()) BigDecimal.ZERO else editTextPrice.text.toString().toBigDecimal()

                    // Add a new item to the list or else updates the right one.
                    item.items?.add(ShoppingListItem(item.items?.size ?: 0, mName.toString(), 1, mPrice, false))

                    model.updateShoppingList(item)
                    dg.dismiss()
                }
                return true // Right key
            }
            return false // Wrong key
        }

        dg = AlertDialog.Builder(context).apply {
            setView(viewInflated)

            // Returns true if pressed key is the correct one.
            editTextName.setOnEditorActionListener { _, actionId, _ -> innerCheck(actionId) }
            editTextPrice.setOnEditorActionListener { _, actionId, _ -> innerCheck(actionId) }
        }.show()
    }
}