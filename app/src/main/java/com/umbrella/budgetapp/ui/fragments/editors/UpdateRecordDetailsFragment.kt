package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.database.viewmodels.StoreViewModel
import com.umbrella.budgetapp.databinding.DataRecordDetailsBinding
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

class UpdateRecordDetailsFragment : ExtendedFragment(R.layout.data_record_details), Edit {
    private val binding by viewBinding(DataRecordDetailsBinding::bind)

    private companion object {
        const val MIN_AMOUNT = 0.01f
    }

    private val model by viewModels<RecordViewModel>()

    private lateinit var extRecord: ExtendedRecord

    private val type: Type

    private var editData = Record(id = 0L)

    private var bundle : Array<String>? = null

    init {
        val args : UpdateRecordDetailsFragmentArgs by navArgs()

        type = checkType(args.recordId)

        if (type == Type.EDIT) {
            extRecord = model.getRecordById(args.recordId)
        } else {
            bundle = args.basicArguments
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == Type.EDIT) editData = extRecord.record

        initData()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            //Populate the spinners.
            Spinners.Currencies(this@UpdateRecordDetailsFragment, dataCardRecordDetailsCurrency)
            Spinners.SimpleSpinner(this@UpdateRecordDetailsFragment, dataCardRecordDetailsPayType, R.array.paymentType)
            Spinners.SimpleSpinner(this@UpdateRecordDetailsFragment, dataCardRecordDetailsType, R.array.type)
            Spinners.Accounts(this@UpdateRecordDetailsFragment, dataCardRecordDetailsAccount)

            if (type == Type.NEW) {
                with(getBasicBundle()) {
                    dataCardRecordDetailsDescription.setText(getOrDefault("description", ""), EDITABLE)
                    dataCardRecordDetailsPayee.setText(getOrDefault("payee", ""), EDITABLE)
                    dataCardRecordDetailsAmount.currencyText("", BigDecimal(get("amount")))
                    dataCardRecordDetailsCategory.text = get("category_name")
                    dataCardRecordDetailsCurrency.setSelection(get("currency_position")!!.toInt())
                    dataCardRecordDetailsType.setSelection(get("type")!!.toInt())
                    dataCardRecordDetailsAccount.setSelection(get("account_position")!!.toInt())

                    if (containsKey("store")) {
                        val storeModel by viewModels<StoreViewModel>()
                        dataCardRecordDetailsStore.text = storeModel.getStoreById(getValue("store").toLong()).store.name
                    }

                    editData.apply {
                        categoryRef = get("category")!!.toLong()
                        currencyRef = get("currency")!!.toLong()
                        accountRef = get("account")!!.toLong()
                        type = get("type")!!.toInt()
                        amount = BigDecimal(get("amount"))
                        payee = getOrDefault("payee", "")
                        description = getOrDefault("description", "")
                        storeRef = getOrDefault("store", "0").toLong()
                    }
                }

            } else {
                dataCardRecordDetailsDescription.setText(extRecord.record.description, EDITABLE)
                dataCardRecordDetailsCategory.text = extRecord.category.name
                dataCardRecordDetailsAmount.currencyText("", extRecord.record.amount!!)
                dataCardRecordDetailsCurrency.setSelection(extRecord.currencyPosition!!)
                dataCardRecordDetailsPayType.setSelection(extRecord.record.paymentType!!)
                dataCardRecordDetailsDateTime.text = DateTimeFormatter().dateTimeFormat(extRecord.record.timestamp!!)
                dataCardRecordDetailsPayee.setText(extRecord.record.payee, EDITABLE)
                dataCardRecordDetailsType.setSelection(extRecord.record.type!!)
                dataCardRecordDetailsAccount.setSelection(extRecord.accountPosition!!)
                dataCardRecordDetailsStore.text = extRecord.storeName
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
            dataCardRecordDetailsDescription.afterTextChangedDelayed { editData.description = it }
            dataCardRecordDetailsPayee.afterTextChangedDelayed { editData.payee = it }

            dataCardRecordDetailsCategory.setOnClickListener {
                // TODO: 15/08/2020 Category Dialog
            }

            dataCardRecordDetailsStore.setOnClickListener {
                // TODO: 15/08/2020 Store Dialog
            }

            dataCardRecordDetailsAmount.setOnClickListener {
                // TODO: 15/08/2020 AmountDialog MIN_VALUE
                //MIN_AMOUNT
            }

            // Open a DateTimePickerDialog with the current date + time.
            dataCardRecordDetailsDateTime.setOnClickListener {
                Dialogs.DateTimePicker(requireContext(), object : Dialogs.DateTimePicker.OnSelectDateTime {
                    override fun dateTimeSelected(timeInMillis: Long) {
                        dataCardRecordDetailsDateTime.text = DateTimeFormatter().dateTimeFormat(timeInMillis)
                        editData.timestamp = timeInMillis
                    }
                }).apply {
                    initialDate(editData.timestamp ?: Calendar.getInstance().timeInMillis)
                    beforeToday()
                    show()
                }
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        // No special requirements. Update EditData with data outside listeners.
        with(binding) {
            editData.apply {
                accountRef = (dataCardRecordDetailsAccount.selectedItem as Account).id
                currencyRef = (dataCardRecordDetailsCurrency.selectedItem as CurrencyAndName).id
                type = dataCardRecordDetailsType.selectedItemPosition
                paymentType = dataCardRecordDetailsPayType.selectedItemPosition
            }
        }

        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addRecord(editData)
        } else if (hasChanges(extRecord.record, editData)) {
            model.updateRecord(editData)
        }
    }

    private fun getBasicBundle() : Map<String, String> {
        val temp = mutableMapOf<String, String>()
        bundle!!.forEach { pair ->
            val base = pair.split('|')
            temp[base[0]] = base[1]
        }
        return temp
    }
}