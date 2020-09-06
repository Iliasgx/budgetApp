package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Store
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.viewmodels.StoreViewModel
import com.umbrella.budgetapp.databinding.DataStoreBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type

class UpdateStoreFragment : ExtendedFragment(R.layout.data_store), Edit {
    private val binding by viewBinding(DataStoreBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 3
    }

    val args : UpdateStoreFragmentArgs by navArgs()

    private val model by viewModels<StoreViewModel>()

    private lateinit var extStore : ExtendedStore

    private lateinit var type: Type

    private var editData = Store(id = 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.storeId)

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> R.string.title_add_store
            Type.EDIT -> R.string.title_edit_store
        })

        model.getStoreById(args.storeId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                extStore = it
                editData = extStore.store
            }
            initData()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            Spinners.Currencies(this@UpdateStoreFragment, dataCardStoreCurrency)

            if (type == Type.EDIT) {
                dataCardStoreName.setText(extStore.store.name, EDITABLE)
                dataCardStoreCurrency.setSelection(extStore.currencyPosition!!)
                dataCardStoreCategory.text = extStore.category.name
                dataCardStoreNote.setText(extStore.store.note, EDITABLE)
            }
        }
        //Set listeners after initializing the data so not listeners would be called already.
        setListeners()
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardStoreName.afterTextChangedDelayed { editData.name = it }
            dataCardStoreNote.afterTextChangedDelayed { editData.note = it }

            dataCardStoreCategory.setOnClickListener {
                // TODO: 14/08/2020 Category DialogFragment
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardStoreName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Store_ErrorMsg_empty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Store_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //Update EditData with data outside listeners.
        editData.currencyRef = (binding.dataCardStoreCurrency.selectedItem as CurrencyAndName).id

        //All requirements are met and all data is loaded. Ready to update data or create new one.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addStore(editData)
        } else if (hasChanges(extStore.store, editData)) {
            model.updateStore(editData)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
        } else {
            findNavController().navigateUp()
        }
        return true
    }
}