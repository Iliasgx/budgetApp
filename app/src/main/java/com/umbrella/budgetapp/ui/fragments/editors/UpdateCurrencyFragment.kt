package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Currency
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedCurrency
import com.umbrella.budgetapp.database.viewmodels.CountryViewModel
import com.umbrella.budgetapp.database.viewmodels.CurrencyViewModel
import com.umbrella.budgetapp.databinding.DataCurrencyBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class UpdateCurrencyFragment : ExtendedFragment(R.layout.data_currency), Edit {
    private val binding by viewBinding(DataCurrencyBinding::bind)

    private companion object {
        val decimalFormat = DecimalFormat("#.###0", DecimalFormatSymbols(Locale.GERMAN))
    }

    private val model by viewModels<CurrencyViewModel>()

    private val extCurrency: ExtendedCurrency

    private val type: Type

    private var editData = Currency(id = 0L)

    init {
        val args : UpdateCurrencyFragmentArgs by navArgs()

        type = checkType(args.currencyId)

        extCurrency = model.getCurrencyById(args.currencyId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == Type.EDIT) editData = extCurrency.currency

        initData()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            Spinners.Currencies(this@UpdateCurrencyFragment, dataCardCurrencyName)

            if (type == Type.EDIT) {
                dataCardCurrencyName.setSelection(extCurrency.currency.position!!)

                //Only the 'Rate' field can be modified on edit.
                dataCardCurrencyName.isClickable = false
            }
        }

        //Set listeners after initializing the data so not listeners would be called already.
        //In this case we programmatically active the listeners to fill everything in automatically.
        setListeners()
    }

    /**
     * Set all necessary listeners for the edit screen.
     * Function called after initData to make sure that listeners are not called when initializing data in the views.
     */
    override fun setListeners() {
        with(binding) {
            dataCardCurrencyRate.afterTextChangedDelayed {
                dataCardCurrencyInverseRate.setText(decimalFormat.format(1.div(BigDecimal(it).toDouble())))
                editData.usedRate = BigDecimal(it)
            }

            dataCardCurrencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    val country = when (type) {
                        Type.NEW -> {
                            val countryModel by viewModels<CountryViewModel>()
                            countryModel.getCountryById((dataCardCurrencyName.selectedItem as CurrencyAndName).id!!)
                        }
                        Type.EDIT -> extCurrency.country!!
                    }

                    if (type == Type.NEW) {
                        dataCardCurrencyRate.setText(decimalFormat.format(country.defaultRate))
                    } else {
                        dataCardCurrencyRate.setText(decimalFormat.format(extCurrency.currency.usedRate))
                    }

                    dataCardCurrencyCode.setText(country.name)
                    dataCardCurrencySymbol.setText(country.symbol)
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
            model.addCurrency(editData)
        } else if (hasChanges(extCurrency.currency, editData)) {
            model.updateCurrency(editData)
        }
    }
}