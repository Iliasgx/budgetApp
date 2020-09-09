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
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedCurrency
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
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
import java.util.*

// Screen can only be used to add a new Record, not to edit it. For editing, the Detail Screen is used.
class UpdateRecordBasicFragment : ExtendedFragment(R.layout.data_record_basic), Edit {
    private val binding by viewBinding(DataRecordBasicBinding::bind)

    private companion object {
        const val MIN_AMOUNT = 0.01f
    }

    val args : UpdateRecordBasicFragmentArgs by navArgs()

    private val model by viewModels<RecordViewModel>()

    private var editData = Record(id = 0L)

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
        setTitle(R.string.title_Records)

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
            dataCardRecordBasicCurrency.text = Memory.lastUsedCountry.name

            // Add details of template when a record is created from a template.
            if (receivedTemplate != null) {
                dataCardRecordBasicTitleGroup.check(requireView().findViewWithTag<RadioButton>(receivedTemplate!!.template.type ?: 0).id)
                dataCardRecordBasicAmount.currencyText("", receivedTemplate!!.template.amount!!)
                dataCardRecordBasicCurrency.text = receivedTemplate!!.currencyName
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
                val value = requireActivity().findViewById<RadioButton>(checkedId).tag as Int

                dataCardRecordBasicMark.text = when(value) {
                    0 -> "+"
                    1 -> "-"
                    else -> "@"
                }

                editData.type = value
            }

            //Automatically check the item to call the listeners. This will update the mark directly.
            dataCardRecordBasicTitleGroup.check(dataCardRecordBasicTitleGroup.checkedRadioButtonId)

            dataCardRecordBasicCurrency.setOnClickListener {
                navToDialog(DataLocationType.CURRENCY)

                getNavigationResult<ExtendedCurrency>(R.id.updateRecordBasic, "data") { result ->
                    dataCardRecordBasicCurrency.apply {
                        text = result.country?.name
                        tag = result.currency.position
                    }
                        editData.currencyRef = result.currency.id
                }
            }

            dataCardRecordBasicAccount.setOnClickListener {
                navToDialog(DataLocationType.ACCOUNT)

                getNavigationResult<Account>(R.id.updateRecordBasic, "data") { result ->
                    dataCardRecordBasicAccount.apply {
                        text = result.name
                        tag = result.position
                    }

                    editData.accountRef = result.id
                }
            }

            dataCardRecordBasicCategory.setOnClickListener {
                navToDialog(DataLocationType.CATEGORY)

                getNavigationResult<Category>(R.id.updateRecordBasic, "data") { result ->
                    dataCardRecordBasicCategory.text = result.name

                    editData.categoryRef = result.id
                }
            }

            dataCardRecordBasicTemplate.setOnClickListener {
                navToDialog(DataLocationType.TEMPLATE)

                getNavigationResult<TemplateAndCategory>(R.id.updateRecordBasic, "data") { result ->
                    dataCardRecordBasicTemplate.text = result.template.name

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
        if (editData.amount!!.toFloat() < MIN_AMOUNT) {
            Toast.makeText(requireContext(), getString(R.string.data_Record_errorMsg_amount, MIN_AMOUNT), Toast.LENGTH_SHORT).show()
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
                        Pair("type", editData.paymentType.toString())
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
        } else {
            findNavController().navigateUp()
        }
        return true
    }

    private fun calculatorListeners() {
        val cal = Calculator()

        with(binding) {
            cal.updateListener = object : Calculator.OnUpdateListener {
                override fun onUpdate(value: String) {
                    if (cal.isNegative()) dataCardRecordBasicTitleGroup.check(0)

                    dataCardRecordBasicAmount.text = cal.getAbsValue()
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