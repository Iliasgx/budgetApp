package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.ShoppingList
import com.umbrella.budgetapp.database.viewmodels.ShoppingListViewModel
import com.umbrella.budgetapp.databinding.DataShoppingListBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.interfaces.Edit

class UpdateShoppingListFragment : ExtendedFragment(R.layout.data_shopping_list), Edit {
    private val binding by viewBinding(DataShoppingListBinding::bind)

    private val model by viewModels<ShoppingListViewModel>()

    private var editData = ShoppingList(id = 0L)

    companion object {
        const val MIN_NAME_LENGTH = 4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(ToolBarNavIcon.CANCEL)

        initData()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        setListeners()
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardShoppingListName.afterTextChangedDelayed { editData.name = it }

            dataCardShoppingListCategory.setOnClickListener {
                // TODO: 13/08/2020 Add Category DialogFragment
            }

            dataCardShoppingListStore.setOnClickListener {
                // TODO: 13/08/2020 Add Store DialogFragment
            }

            dataCardShoppingListCreate.setOnClickListener { checkData() }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardShoppingListName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_ShoppingList_ErrorMsg_empty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_ShoppingList_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //All requirements are met.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        model.addShoppingList(editData)
    }
}