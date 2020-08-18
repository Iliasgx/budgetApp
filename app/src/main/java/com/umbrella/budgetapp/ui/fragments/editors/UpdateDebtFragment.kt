package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import androidx.fragment.app.viewModels
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
import com.umbrella.budgetapp.extensions.DateTimeFormatter
import com.umbrella.budgetapp.extensions.Dialogs
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.extensions.currencyText
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

    private val model by viewModels<DebtViewModel>()

    private var extDebt : ExtendedDebt

    private var type: Type

    private var debtType: DebtType

    private var editData = Debt(id = 0L)

    init {
        val args : UpdateDebtFragmentArgs by navArgs()

        type = checkType(args.debtId)

        extDebt = model.getDebtById(args.debtId)

        debtType = when(type) {
            Type.NEW -> DebtType.values()[args.debtType]
            Type.EDIT -> extDebt.debt.debtType!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == Type.NEW) editData = extDebt.debt

        initData()
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
                // TODO: 13/08/2020 AmountDialog with MIN_VALUE
                //MIN_AMOUNT
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
}