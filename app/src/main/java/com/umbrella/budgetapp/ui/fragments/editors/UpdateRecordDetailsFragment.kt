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
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedRecord
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.database.viewmodels.StoreViewModel
import com.umbrella.budgetapp.databinding.DataRecordDetailsBinding
import com.umbrella.budgetapp.extensions.*
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType.CATEGORY
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType.STORE
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal
import java.util.*

class UpdateRecordDetailsFragment : ExtendedFragment(R.layout.data_record_details), Edit {
    private val binding by viewBinding(DataRecordDetailsBinding::bind)

    private companion object {
        const val MIN_AMOUNT = 0.01f
    }

    val args : UpdateRecordDetailsFragmentArgs by navArgs()

    private val model by viewModels<RecordViewModel>()

    private lateinit var extRecord: ExtendedRecord

    private lateinit var type: Type

    private var editData = Record()

    private var bundle : Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.recordId)

        setToolbar(ToolBarNavIcon.CANCEL)

        model.getRecordById(args.recordId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                extRecord = it
                editData = extRecord.record.copy()

                setTitle(getString(R.string.title_addChange_record_value, DefaultCountries().getCountryById(it.countryRef).symbol, it.record.amount!!.toDouble()))
            } else {
                bundle = args.basicArguments
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
                        storeModel.getStoreById(getValue("store").toLong()).observe(viewLifecycleOwner, Observer {
                            dataCardRecordDetailsStore.text = it.store.name
                        })
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
                findNavController().navigate(UpdateRecordDetailsFragmentDirections.globalDataListDialog(CATEGORY))

                getNavigationResult<Category>(R.id.updateRecordDetails, "category_data") { result ->
                    editData.categoryRef = result.id
                    dataCardRecordDetailsCategory.text = result.name
                }
            }

            dataCardRecordDetailsStore.setOnClickListener {
                findNavController().navigate(UpdateRecordDetailsFragmentDirections.globalDataListDialog(STORE))

                getNavigationResult<ExtendedStore>(R.id.updateRecordDetails, "store_data") { result ->
                    editData.storeRef = result.store.id
                    dataCardRecordDetailsStore.text = result.store.name
                }
            }

            dataCardRecordDetailsAmount.setOnClickListener {
                findNavController().navigate(UpdateRecordDetailsFragmentDirections.globalDialogAmount(editData.amount?.toEngineeringString(), MIN_AMOUNT))

                //Result as setTitle change
            }

            getNavigationResult<String>(R.id.updateRecordDetails, "amount") { result ->
                editData.amount = BigDecimal(result)

                dataCardRecordDetailsAmount.text = result

                val symbol = if (type == Type.NEW) Memory.lastUsedCountry.symbol else DefaultCountries().getCountryById(extRecord.countryRef).symbol
                setTitle(getString(R.string.title_addChange_record_value, symbol, BigDecimal(result).toDouble()))
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
                accountRef = (dataCardRecordDetailsAccount.tag as Account).id
                currencyRef = (dataCardRecordDetailsCurrency.tag as CurrencyAndName).id
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
        var oldVal = BigDecimal.ZERO

        if (type == Type.NEW) {
            model.addRecord(editData)
            oldVal = BigDecimal.ZERO
        } else if (hasChanges(extRecord.record, editData)) {
            model.updateRecord(editData)
            oldVal = extRecord.record.amount!!
        }

        val accountModel by viewModels<AccountViewModel>()
        accountModel.getAccountById(editData.accountRef!!).observe(viewLifecycleOwner, Observer {
            val tempAccount = it.account

            val diff = editData.amount!!.min(oldVal)
            tempAccount.currentValue = tempAccount.currentValue!!.add(diff)

            accountModel.getAccountById(editData.accountRef!!).removeObservers(viewLifecycleOwner)
            accountModel.updateAccount(tempAccount)
        })

        navigateUp()
    }

    private fun getBasicBundle() : Map<String, String> {
        val temp = mutableMapOf<String, String>()
        bundle!!.forEach { pair ->
            val base = pair.split('|')
            temp[base[0]] = base[1]
        }
        return temp
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
            return true
        }
        return false
    }
}