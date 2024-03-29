package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.database.viewmodels.TemplateViewModel
import com.umbrella.budgetapp.databinding.DataRecordBasicBinding
import com.umbrella.budgetapp.enums.CalculatorFunction.*
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.components.Calculator
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType
import com.umbrella.budgetapp.ui.interfaces.Edit
import java.math.BigDecimal
import java.util.*

// Screen can only be used to add a new Record, not to edit it. For editing, the Detail Screen is used.
class UpdateRecordBasicFragment : ExtendedFragment(R.layout.data_record_basic), Edit {
    private val binding by viewBinding(DataRecordBasicBinding::bind)

    private companion object {
        const val MIN_AMOUNT = 0.01
    }

    val args : UpdateRecordBasicFragmentArgs by navArgs()

    private val model by viewModels<RecordViewModel>()

    private var editData = Record(amount = BigDecimal.ZERO)

    // Used when user navigated to creating a record from a template. Not used for the Template Dialog in this screen.
    private var receivedTemplate : ExtendedTemplate? = null

    // Used to identify if this is the first time the initializer is called, making sure listeners are not called twice.
    private var firstInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(ToolBarNavIcon.CANCEL)

        if (args.templateId != 0L) {
            val tempModel by viewModels<TemplateViewModel>()
            val singleDao = tempModel.getTemplateById(args.templateId)

            singleDao.observe(viewLifecycleOwner, Observer {
                receivedTemplate = it
                initData()
                singleDao.removeObservers(viewLifecycleOwner)
            })
        } else {
            initData()
        }
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
            // Add details of template when a record is created from a template.
            if (receivedTemplate != null) {
                dataCardRecordBasicTitleGroup.check(requireView().findViewWithTag<RadioButton>(receivedTemplate!!.template.type!!.toString()).id)
                dataCardRecordBasicAmount.currencyText("", receivedTemplate!!.template.amount!!)
                dataCardRecordBasicCurrency.text = DefaultCountries().getCountryById(receivedTemplate!!.countryRef).name
                dataCardRecordBasicCurrency.tag = receivedTemplate!!.currencyPosition
                dataCardRecordBasicAccount.text = receivedTemplate!!.accountName
                dataCardRecordBasicCategory.text = receivedTemplate!!.category.name

                //Transfer data from template to Record.
                editData.apply {
                    amount = receivedTemplate!!.template.amount
                    currencyRef = receivedTemplate!!.currencyId
                    accountRef = receivedTemplate!!.accountId
                    categoryRef = receivedTemplate!!.category.id
                    storeRef = receivedTemplate!!.storeId
                    description = receivedTemplate!!.template.note
                    payee = receivedTemplate!!.template.payee
                    paymentType = receivedTemplate!!.template.paymentType
                }
            }
        }

        if (firstInit) {
            setListeners()
            firstInit = false // First initializer finished.
        }
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            // Update mark when type is changed.
            dataCardRecordBasicTitleGroup.setOnCheckedChangeListener { _, checkedId ->
                val value = (requireActivity().findViewById<RadioButton>(checkedId).tag as String).toInt()

                dataCardRecordBasicMark.text = when(value) {
                    0 -> "+"
                    else -> "-"
                }

                editData.type = value
            }
            
            //Automatically check the item to call the listeners. This will update the mark directly.
            dataCardRecordBasicTitleGroup.check(R.id.data_Card_RecordBasic_Title2)

            dataCardRecordBasicCurrency.setOnClickListener {
                navToDialog(DataLocationType.CURRENCY)

                getNavigationResult<Currency>(R.id.updateRecordBasic, "currency_data") { result ->
                    dataCardRecordBasicCurrency.apply {
                        text = DefaultCountries().getCountryById(result.countryRef).name
                        tag = result.position
                    }
                        editData.currencyRef = result.id
                }
            }

            dataCardRecordBasicAccount.setOnClickListener {
                navToDialog(DataLocationType.ACCOUNT)

                getNavigationResult<Account>(R.id.updateRecordBasic, "account_data") { result ->
                    dataCardRecordBasicAccount.apply {
                        text = result.name
                        tag = result.position
                    }

                    editData.accountRef = result.id
                }
            }

            dataCardRecordBasicCategory.setOnClickListener {
                navToDialog(DataLocationType.CATEGORY)

                getNavigationResult<Category>(R.id.updateRecordBasic, "category_data") { result ->
                    dataCardRecordBasicCategory.text = result.name

                    editData.categoryRef = result.id
                }
            }

            dataCardRecordBasicTemplate.setOnClickListener {
                navToDialog(DataLocationType.TEMPLATE)

                getNavigationResult<TemplateAndCategory>(R.id.updateRecordBasic, "template_data") { result ->
                    dataCardRecordBasicTemplate.text = result.template.name

                    // Need to get the full template because @result has not all fields needed. (Fields were limited in dialog)
                    val tempMod by viewModels<TemplateViewModel>()
                    val singleDao = tempMod.getTemplateById(result.template.id!!)

                    singleDao.observe(viewLifecycleOwner, Observer {
                        receivedTemplate = it
                        initData()
                        singleDao.removeObservers(viewLifecycleOwner)
                    })
                }
            }

            //Save data to Bundle and transfer it to the detail screen. Used when the user wants to add additional info.
            dataCardRecordBasicDetails.setOnClickListener { exportToDetails() }
        }

        //Set all listeners for the calculator.
        calculatorListeners()
    }

    private fun navToDialog(dataType: DataLocationType) {
        findNavController().navigate(UpdateRecordBasicFragmentDirections.globalDataListDialog(dataType))
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        if (editData.amount!!.toDouble() < MIN_AMOUNT.toDouble()) {
            Toast.makeText(requireContext(), getString(R.string.data_Record_errorMsg_amount, MIN_AMOUNT.toDouble()), Toast.LENGTH_SHORT).show()
            return
        }

        editData.timestamp = Calendar.getInstance().timeInMillis

        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        model.addRecord(editData)

        val accountModel by viewModels<AccountViewModel>()
        accountModel.getAccountById(editData.accountRef!!).observe(viewLifecycleOwner, Observer {
            val tempAccount = it.account
            tempAccount.currentValue = tempAccount.currentValue!!.add(editData.amount)

            accountModel.getAccountById(editData.accountRef!!).removeObservers(viewLifecycleOwner)
            accountModel.updateAccount(tempAccount)
        })

        navigateUp()
    }

    private fun exportToDetails() {
        //Navigate to detail screen with the additional bundle of data. When created from Template, also includes that data.
        findNavController().navigate(UpdateRecordBasicFragmentDirections.updateRecordBasicToUpdateRecordDetails(
                basicArguments = addBundle(
                        Pair("amount", editData.amount.toString()),
                        Pair("currency", editData.currencyRef.toString()),
                        Pair("currency_position", binding.dataCardRecordBasicCurrency.tag.toString()),
                        Pair("account", editData.accountRef.toString()),
                        Pair("account_position", binding.dataCardRecordBasicAccount.tag.toString()),
                        Pair("category", editData.categoryRef.toString()),
                        Pair("category_name", binding.dataCardRecordBasicCategory.text.toString()),
                        Pair("type", editData.type.toString())
                ) + if (receivedTemplate != null) {
                    addBundle(
                            Pair("description", receivedTemplate!!.template.note.toString()),
                            Pair("store", receivedTemplate!!.template.storeRef.toString()),
                            Pair("payee", receivedTemplate!!.template.payee.toString())
                    )
                } else emptyArray()
        ))
    }

    //Safe the Pairs of data to a Array of strings to add them with the SafeArgs.
    private fun addBundle(vararg pair: Pair<String, String>) : Array<String> {
        val temp = mutableListOf<String>()
        pair.forEach { temp.add("${it.first}|${it.second}") }
        return temp.toTypedArray()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
            return true
        }
        return false
    }

    private fun calculatorListeners() {
        val cal = Calculator()

        with(binding) {
            cal.updateListener = object : Calculator.OnUpdateListener {
                override fun onUpdate(value: String) {
                    if (cal.isNegative()) dataCardRecordBasicTitleGroup.check(requireView().findViewWithTag<RadioButton>("1").id)

                    dataCardRecordBasicAmount.text = cal.getAbsValue()
                    editData.amount = BigDecimal(cal.getAbsValue())
                }
            }

            dataCardRecordBasicZero.setOnClickListener  { cal.calculate(ZERO) }
            dataCardRecordBasicOne.setOnClickListener   { cal.calculate(ONE) }
            dataCardRecordBasicTwo.setOnClickListener   { cal.calculate(TWO) }
            dataCardRecordBasicThree.setOnClickListener { cal.calculate(THREE) }
            dataCardRecordBasicFour.setOnClickListener  { cal.calculate(FOUR) }
            dataCardRecordBasicFive.setOnClickListener  { cal.calculate(FIVE) }
            dataCardRecordBasicSix.setOnClickListener   { cal.calculate(SIX) }
            dataCardRecordBasicSeven.setOnClickListener { cal.calculate(SEVEN) }
            dataCardRecordBasicEight.setOnClickListener { cal.calculate(EIGHT) }
            dataCardRecordBasicNine.setOnClickListener  { cal.calculate(NINE) }

            dataCardRecordBasicDot.setOnClickListener       { cal.calculate(DOT) }
            dataCardRecordBasicBackspace.setOnClickListener { cal.calculate(REMOVE) }

            dataCardRecordBasicDivide.setOnClickListener    { cal.calculate(DIVIDE) }
            dataCardRecordBasicMultiply.setOnClickListener  { cal.calculate(MULTIPLY) }
            dataCardRecordBasicSubtract.setOnClickListener  { cal.calculate(SUBTRACT) }
            dataCardRecordBasicAdd.setOnClickListener       { cal.calculate(ADD) }
            dataCardRecordBasicEquals.setOnClickListener    { cal.calculate(EQUALS) }
        }
    }
}