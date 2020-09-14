package com.umbrella.budgetapp.ui.fragments.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView.BufferType.EDITABLE
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.ShoppingListItemsAdapter
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.collections.ShoppingListItem
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.fragments.editors.UpdateShoppingListFragment
import java.math.BigDecimal
import java.util.*

class ShoppingListItemsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    val model by viewModels<ShoppingListViewModel>()

    val args : ShoppingListItemsFragmentArgs by navArgs()

    private lateinit var adapter: ShoppingListItemsAdapter

    private var shoppingList = ShoppingList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        model.getShoppingListById(args.shoppingListId).observe(viewLifecycleOwner, Observer {
            setTitle(it.name!!)

            shoppingList = it

            if (!it.items.isNullOrEmpty()) {
                adapter.setData(it.items!!.toList())
            }
        })

        binding.fragmentFloatingActionButton.apply {
            isVisible = true
            setOnClickListener { addItem() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminder_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView() {
        adapter = ShoppingListItemsAdapter(object : ShoppingListItemsAdapter.CallBack {
            override fun onItemClick(position: Int) {
                addItem(shoppingList.items!![position])
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

    private fun createRecord() : Boolean {
        findNavController().navigate(ShoppingListItemsFragmentDirections.globalAddRecord(
                0L,
                shoppingList.categoryRef!!,
                shoppingList.items?.filter { item -> item.checked }?.sumByDouble { item -> item.amount.multiply(item.number.toBigDecimal()).toDouble() }.toString(),
                0L,
                shoppingList.name!!
        ))

        getNavigationResult<Boolean>(R.id.shoppingListItems, "record") {
            //Record is created for selected items, deselect all for next time.
            shoppingList.items!!.forEach { item -> item.checked = false }
            model.updateShoppingList(shoppingList)
            navigateUp()
        }

        return true
    }

    // Function for adding a new item or updating an existing item.
    // When item is @null, function identifies it as adding a new one, else updating.
    private fun addItem(item: ShoppingListItem? = null) {
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
                    if (item == null) {
                        shoppingList.items?.add(ShoppingListItem(shoppingList.items?.size ?: 0, mName.toString(), 1, mPrice, false))
                    } else {
                        shoppingList.items!![item.position].apply {
                            name = mName.toString()
                            amount = mPrice
                        }
                    }
                    model.updateShoppingList(shoppingList)
                    dg.dismiss()
                }
                return true // Right key
            }
            return false // Wrong key
        }


        dg = AlertDialog.Builder(context).apply {
            setView(viewInflated)

            // When updating, fill already defined values in.
            if (item != null) {
                editTextName.setText(item.name, EDITABLE)
                editTextPrice.setText(String.format("%.2f", item.amount), EDITABLE)
            }

            // Returns true if pressed key is the correct one.
            editTextPrice.setOnEditorActionListener { _, actionId, _ -> innerCheck(actionId)}
            editTextName.setOnEditorActionListener { _, actionId, _ -> innerCheck(actionId) }
        }.show()
    }

    @SuppressLint("DefaultLocale")
    private fun renameList() : Boolean {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.dialog_rename_title))

            val viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_rename, view as ViewGroup, false)

            val editText = viewInflated.findViewById<TextInputEditText>(R.id.dialog_rename_name)

            setView(viewInflated)

            setPositiveButton(android.R.string.ok) { dialog, _ ->
                val mText = editText.text?.trim()

                if (mText.isNullOrBlank() || mText.length < UpdateShoppingListFragment.MIN_NAME_LENGTH) {
                    editText.error = getString(R.string.dialog_rename_error_name, UpdateShoppingListFragment.MIN_NAME_LENGTH)
                } else {
                    shoppingList.name = mText.toString().toLowerCase(Locale.ROOT).capitalize()
                    model.updateShoppingList(shoppingList)
                    dialog.dismiss()
                }
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.show()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuLayout_ReminderOptions_Reminder -> {
                // TODO-UPCOMING: Reminder Dialog (possible, not required)
                true
            }
            R.id.menuLayout_ReminderOptions_Rename -> { renameList() }
            R.id.menuLayout_ReminderOptions_CreateRecord -> { createRecord() }
            R.id.menuLayout_ReminderOptions_DeleteList -> {
                model.getShoppingListById(shoppingList.id!!).removeObservers(viewLifecycleOwner)
                model.removeShoppingList(shoppingList)
                navigateUp()
            }
            else -> false
        }

    }
}