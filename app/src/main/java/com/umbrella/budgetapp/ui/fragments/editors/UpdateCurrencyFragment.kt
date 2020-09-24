package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.defaults.DefaultCountries
import com.umbrella.budgetapp.database.viewmodels.CurrencyViewModel
import com.umbrella.budgetapp.databinding.DataCurrencyBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal
import java.math.RoundingMode

class UpdateCurrencyFragment : ExtendedFragment(R.layout.data_currency), Edit {
    private val binding by viewBinding(DataCurrencyBinding::bind)

    val args : UpdateCurrencyFragmentArgs by navArgs()

    private val model by viewModels<CurrencyViewModel>()

    private lateinit var currency: Currency

    private lateinit var type: Type

    private var editData = Currency()

    private val def = DefaultCountries()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.currencyId)

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> R.string.title_add_currency
            Type.EDIT -> R.string.title_edit_currency
        })

        model.getCurrencyById(args.currencyId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                currency = it
                editData = currency.copy()
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
            setUpSpinner()

            if (type == Type.EDIT) {
                dataCardCurrencyName.setSelectionById(currency.countryRef!!)

                //Only the 'Rate' field can be modified on edit.
                dataCardCurrencyName.isClickable = false
            }
        }

        //Set listeners after initializing the data so not listeners would be called already.
        //In this case we programmatically active the listeners to fill everything in automatically.
        setListeners()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, DefaultCountries().getNamesOfCountries())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dataCardCurrencyName.adapter = adapter
    }

    private fun Spinner.setSelectionById(id: Long) = setSelection(def.getIndexOf(id))

    private fun EditText.decimalText(bg: BigDecimal) { setText(String.format("%.4f", bg), EDITABLE) }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardCurrencyRate.afterTextChangedDelayed {
                dataCardCurrencyInverseRate.decimalText(BigDecimal.ONE.divide(it.toBigDecimal(), 4, RoundingMode.HALF_UP))
                editData.usedRate = BigDecimal(it)
            }

            dataCardCurrencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    val country = when (type) {
                        Type.NEW -> def.getCountryByPosition(position)
                        Type.EDIT -> def.getCountryById(currency.countryRef)
                    }

                    if (type == Type.NEW) {
                        dataCardCurrencyRate.decimalText(country.defaultRate)
                        editData.usedRate = country.defaultRate
                    } else {
                        val usableRate = currency.usedRate ?: country.defaultRate
                        dataCardCurrencyRate.decimalText(usableRate)
                        editData.usedRate = usableRate
                    }

                    dataCardCurrencyCode.setText(country.name)
                    dataCardCurrencySymbol.setText(country.symbol)

                    editData.countryRef = def.getCountryByPosition(position).id
                }
            }

            dataCardCurrencyName.setSelection(dataCardCurrencyName.selectedItemPosition)
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            // Used the count to identify the position of the next item.
            editData.position = args.nextPosition
            model.addCurrency(editData)
        } else if (hasChanges(currency, editData)) {
            model.updateCurrency(editData)
        }
        navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
            return true
        }
        return false
    }
}