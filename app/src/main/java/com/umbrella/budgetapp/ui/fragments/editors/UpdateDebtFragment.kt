package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Debt
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedDebt
import com.umbrella.budgetapp.database.viewmodels.DebtViewModel
import com.umbrella.budgetapp.databinding.DataDebtBinding
import com.umbrella.budgetapp.enums.DebtType
import com.umbrella.budgetapp.extensions.*
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal
import java.util.*

class UpdateDebtFragment : ExtendedFragment(R.layout.data_debt), Edit {
    private val binding by viewBinding(DataDebtBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 3
        const val MIN_AMOUNT = 1.0f
    }
    val args : UpdateDebtFragmentArgs by navArgs()

    private val model by viewModels<DebtViewModel>()

    private lateinit var extDebt : ExtendedDebt

    private lateinit var type: Type

    private lateinit var debtType: DebtType

    private var editData = Debt(id = 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.debtId)

        debtType = when(type) {
            Type.NEW -> DebtType.values()[args.debtType]
            Type.EDIT -> extDebt.debt.debtType!!
        }

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> {
                when (debtType) {
                    DebtType.LENT -> R.string.title_add_debt_lent
                    DebtType.BORROWED -> R.string.title_add_debt_borrowed
                }
            }
            Type.EDIT -> R.string.title_edit_debt
        })

        model.getDebtById(args.debtId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                extDebt = it
                editData = extDebt.debt
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
            //Populate the spinners.
            Spinners.Accounts(this@UpdateDebtFragment, dataCardDebtAccount)
            Spinners.Currencies(this@UpdateDebtFragment, dataCardDebtCurrency)

            if (type == Type.NEW) {
                dataCardDebtAmount.currencyText(Memory.lastUsedCountry.symbol!!, BigDecimal.ZERO)
                dataCardDebtDate.text = DateTimeFormatter().dateTimeFormat(Calendar.getInstance().timeInMillis)
            } else {
                dataCardDebtName.setText(extDebt.debt.name, EDITABLE)
                dataCardDebtDescription.setText(extDebt.debt.description, EDITABLE)
                dataCardDebtCategory.text = extDebt.category.name
                dataCardDebtAccount.setSelection(extDebt.accountPosition!!)
                dataCardDebtAmount.currencyText("", extDebt.debt.amount!!)
                dataCardDebtCurrency.setSelection(extDebt.currencyPosition!!)
                dataCardDebtDate.text = DateTimeFormatter().dateTimeFormat(extDebt.debt.timestamp!!)
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
            dataCardDebtName.afterTextChangedDelayed { editData.name = it }
            dataCardDebtDescription.afterTextChangedDelayed { editData.description = it }

            dataCardDebtCategory.setOnClickListener {
                // TODO: 13/08/2020 Category DialogFragment popUp
            }

            dataCardDebtAmount.setOnClickListener {
                findNavController().navigate(UpdateDebtFragmentDirections.globalDialogAmount(editData.amount?.toEngineeringString(), MIN_AMOUNT))
            }

            getNavigationResult<String>(R.id.updateDebt, "amount") { result ->
                editData.amount = BigDecimal(result)
                dataCardDebtAmount.text = result
            }

            dataCardDebtDate.setOnClickListener {
                Dialogs.DateTimePicker(requireContext(), object : Dialogs.DateTimePicker.OnSelectDateTime {
                    override fun dateTimeSelected(timeInMillis: Long) {
                        dataCardDebtDate.text = DateTimeFormatter().dateTimeFormat(timeInMillis)
                        editData.timestamp = timeInMillis
                    }
                }).apply {
                    initialDate(editData.timestamp ?: Calendar.getInstance().timeInMillis)
                    show()
                }
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardDebtName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Debt_errorMsg_nameEmpty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Debt_errorMsg_nameTooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        if (editData.amount!!.toFloat() < MIN_AMOUNT) {
            Toast.makeText(requireContext(), getString(R.string.data_Debt_errorMsg_amountError, MIN_AMOUNT), Toast.LENGTH_SHORT).show()
            return
        }

        //Update EditData with data outside listeners.
        with(binding) {
            editData.apply {
                accountRef = (dataCardDebtAccount.selectedItem as Account).id
                currencyRef = (dataCardDebtCurrency.selectedItem as CurrencyAndName).id
            }
        }

        //All requirements are met and data is loaded.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addDebt(editData)
        } else if (hasChanges(extDebt.debt, editData)) {
            model.updateDebt(editData)
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