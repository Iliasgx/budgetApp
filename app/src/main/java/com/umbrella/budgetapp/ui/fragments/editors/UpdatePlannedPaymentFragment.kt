package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.PlannedPayment
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedPlannedPayment
import com.umbrella.budgetapp.database.viewmodels.PlannedPaymentViewModel
import com.umbrella.budgetapp.databinding.DataPlannedPaymentBinding
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

class UpdatePlannedPaymentFragment : ExtendedFragment(R.layout.data_planned_payment), Edit {
    private val binding by viewBinding(DataPlannedPaymentBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 4
        const val MIN_AMOUNT = 1.0f
    }

    private val model by viewModels<PlannedPaymentViewModel>()

    private var extPlannedPayment : ExtendedPlannedPayment

    private val type: Type

    private var editData = PlannedPayment(id = 0L)

    init {
        val args : UpdatePlannedPaymentFragmentArgs by navArgs()

        type = checkType(args.plannedPaymentId)

        extPlannedPayment = model.getPlannedPaymentById(args.plannedPaymentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == Type.NEW) editData = extPlannedPayment.plannedPayment

        initData()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            //Populate the spinners.
            Spinners.Currencies(this@UpdatePlannedPaymentFragment, dataCardPlannedPaymentCurrency)
            Spinners.Accounts(this@UpdatePlannedPaymentFragment, dataCardPlannedPaymentAccount)
            Spinners.SimpleSpinner(this@UpdatePlannedPaymentFragment, dataCardPlannedPaymentPayType, R.array.paymentType)
            Spinners.SimpleSpinner(this@UpdatePlannedPaymentFragment, dataCardPlannedPaymentType, R.array.type)
            Spinners.SimpleSpinner(this@UpdatePlannedPaymentFragment, dataCardPlannedPaymentReminder, R.array.reminderOptions)

            if (type == Type.NEW) {
                dataCardPlannedPaymentAmount.currencyText(Memory.lastUsedCountry.symbol!!, BigDecimal.ZERO)
                dataCardPlannedPaymentFrom.text = DateTimeFormatter().dateFormat(Calendar.getInstance().timeInMillis)
            } else {
                dataCardPlannedPaymentName.setText(extPlannedPayment.plannedPayment.name, EDITABLE)
                dataCardPlannedPaymentFrom.text = DateTimeFormatter().dateFormat(extPlannedPayment.plannedPayment.startDate!!)
                dataCardPlannedPaymentReminder.setSelection(extPlannedPayment.plannedPayment.reminderOptions!!)
                // TODO: 13/08/2020 find out how to display
                //dataCardPlannedPaymentFrequency.text = extPlannedPayment.plannedPayment.frequency?

                dataCardPlannedPaymentAmount.currencyText("", extPlannedPayment.plannedPayment.amount!!)
                dataCardPlannedPaymentCurrency.setSelection(extPlannedPayment.currencyPosition!!)
                dataCardPlannedPaymentAccount.setSelection(extPlannedPayment.accountPosition!!)
                dataCardPlannedPaymentType.setSelection(extPlannedPayment.plannedPayment.type!!)
                dataCardPlannedPaymentCategory.text = extPlannedPayment.categoryName
                dataCardPlannedPaymentPayType.setSelection(extPlannedPayment.plannedPayment.paymentType!!)
                dataCardPlannedPaymentPayee.setText(extPlannedPayment.plannedPayment.payee, EDITABLE)
                dataCardPlannedPaymentNote.setText(extPlannedPayment.plannedPayment.note, EDITABLE)
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
            dataCardPlannedPaymentName.afterTextChangedDelayed { editData.name = it }
            dataCardPlannedPaymentPayee.afterTextChangedDelayed { editData.payee = it }
            dataCardPlannedPaymentNote.afterTextChangedDelayed { editData.note = it }

            //Open a DatePickerDialog with the current date.
            dataCardPlannedPaymentFrom.setOnClickListener {
                Dialogs.DatePicker(requireContext(), object: Dialogs.DatePicker.OnSelectDate {
                    override fun dateSelected(timeInMillis: Long) {
                        dataCardPlannedPaymentFrom.text = DateTimeFormatter().dateFormat(timeInMillis)
                        editData.startDate = timeInMillis
                    }
                }).apply {
                    initialDate(editData.startDate ?: Calendar.getInstance().timeInMillis)
                    fromToday()
                    show()
                }
            }

            dataCardPlannedPaymentAmount.setOnClickListener {
                // TODO: 13/08/2020 AmountDialog + MIN_VALUE
            }

            dataCardPlannedPaymentCategory.setOnClickListener {
                // TODO: 13/08/2020 Categories
                //Category -> DialogFragment with select
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardPlannedPaymentName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_PlannedPayment_errorMsg_nameEmpty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_PlannedPayment_errorMsg_nameTooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //Update EditData with data outside listeners.
        with(binding) {
            editData.apply {
                reminderOptions = dataCardPlannedPaymentReminder.selectedItemPosition
                currencyRef = (dataCardPlannedPaymentCurrency.selectedItem as CurrencyAndName).id
                accountRef = (dataCardPlannedPaymentAccount.selectedItem as Account).id
                type = dataCardPlannedPaymentType.selectedItemPosition
                paymentType = dataCardPlannedPaymentPayType.selectedItemPosition
            }
        }

        //All requirements are met and all data is loaded. Ready to update data or create new one.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addPlannedPayment(editData)
        } else if (hasChanges(extPlannedPayment.plannedPayment, editData)) {
            model.updatePlannedPayment(editData)
        }
    }
}