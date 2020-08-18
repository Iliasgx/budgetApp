package com.umbrella.budgetapp.ui.fragments.editors

import android.os.Bundle
import android.view.View
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedAccount
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.databinding.DataAccountBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal

class UpdateAccountFragment: ExtendedFragment(R.layout.data_account), Edit {
    private val binding by viewBinding(DataAccountBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 2
    }

    private val model by viewModels<AccountViewModel>()

    private var extAccount : ExtendedAccount

    private var type: Type

    private var editData = Account(id = 0L)

    init {
        val args : UpdateAccountFragmentArgs by navArgs()

        type = checkType(args.accountId)

        extAccount = model.getAccountById(args.accountId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == Type.NEW) editData = extAccount.account

        initData()
    }

    /**
     * Populate necessary views and implement the default data.
     */
    override fun initData() {
        with(binding) {
            //Populate the spinners.
            Spinners.SimpleSpinner(this@UpdateAccountFragment, dataCardAccountType, R.array.accountType)
            Spinners.Currencies(this@UpdateAccountFragment, dataCardAccountCurrency)
            Spinners.Colors(this@UpdateAccountFragment, dataCardAccountColor, Spinners.Colors.Size.LARGE)

            if (type == Type.NEW) {
                dataCardAccountCurrValue.currencyText(Memory.lastUsedCountry.symbol!!, BigDecimal.ZERO)
                dataCardAccountExcludeStats.isChecked = false
            } else {
                dataCardAccountName.setText(extAccount.account.name, EDITABLE)
                dataCardAccountType.setSelection(extAccount.account.type!!)
                dataCardAccountCurrValue.currencyText(extAccount.currencySymbol!!, extAccount.account.currentValue!!)
                dataCardAccountCurrency.setSelection(extAccount.currencyPosition!!)
                dataCardAccountColor.setSelection(extAccount.account.color!!)
                dataCardAccountExcludeStats.isEnabled = extAccount.account.excludeStats!!
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
            dataCardAccountName.afterTextChangedDelayed { editData.name = it }

            dataCardAccountCurrValue.setOnClickListener {
                // TODO: 13/08/2020 AmountDialog
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardAccountName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Account_errorMsg_nameEmpty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Account_errorMsg_nameTooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //Update EditData with data outside listeners.
        with(binding) {
            editData.apply {
                type = dataCardAccountType.selectedItemPosition
                currencyRef = (dataCardAccountCurrency.selectedItem as CurrencyAndName).id
                color = dataCardAccountColor.selectedItemPosition
                excludeStats = dataCardAccountExcludeStats.isChecked
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
            model.addAccount(editData)
        } else if (hasChanges(extAccount.account, editData)) {
            model.updateAccount(editData)
        }
    }
}