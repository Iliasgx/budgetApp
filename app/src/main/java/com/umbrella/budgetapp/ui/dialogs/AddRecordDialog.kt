package com.umbrella.budgetapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.BufferType.EDITABLE
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Record
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.database.viewmodels.RecordViewModel
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.extensions.setNavigationResult
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType.CATEGORY
import kotlinx.android.synthetic.main.dialog_record.*
import java.math.BigDecimal
import java.time.Instant

class AddRecordDialog : DialogFragment() {

    private val args by navArgs<AddRecordDialogArgs>()

    companion object {
        const val MIN_AMOUNT = 0.01f
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog_Record_Create.setOnClickListener { addRecord() }
        dialog_Record_Cancel.setOnClickListener { dismiss() }

        initData()
    }

    private fun initData() {
        val acc = Spinners.Accounts(this, dialog_Record_Account)
        val curr = Spinners.Currencies(this, dialog_Record_Currency)

        dialog_Record_Account.setSelection(acc.getPosition(args.accountRef))
        dialog_Record_Currency.setSelection(curr.getPosition(args.currencyRef))

        // TODO: 08/09/2020 Add first default category

        dialog_Record_Amount.currencyText("", BigDecimal(args.amount))
        dialog_Record_Note.setText(args.note, EDITABLE)

        dialog_Record_Category.setOnClickListener {
            findNavController().navigate(AddRecordDialogDirections.globalDataListDialog(CATEGORY))
        }

        dialog_Record_Amount.setOnClickListener {
            findNavController().navigate(AddRecordDialogDirections.globalDialogAmount((it as TextView).text.toString(), MIN_AMOUNT))
        }

        getNavigationResult<Category>(R.id.addRecordDialog, "category_data") { result ->
            dialog_Record_Category.apply {
                text = result.name
                tag = result.id
            }

        }

        getNavigationResult<String>(R.id.addRecordDialog, "amount") { result ->
            dialog_Record_Amount.text = result
        }
    }


    private fun addRecord() {
        if (BigDecimal(dialog_Record_Amount.text.toString()).compareTo(MIN_AMOUNT.toBigDecimal()) == -1) {
            Toast.makeText(context, getString(R.string.dialog_Record_error_amount, MIN_AMOUNT), Toast.LENGTH_SHORT).show()
            return
        }

        val model by viewModels<RecordViewModel>()

        model.addRecord(Record(
                null,
                dialog_Record_Note.text.toString(),
                "",
                dialog_Record_Category.tag as Long,
                (dialog_Record_Account.tag as Account).id,
                0L,
                (dialog_Record_Currency.tag as CurrencyAndName).id,
                if (BigDecimal(dialog_Record_Amount.text.toString()).signum() < 1) 1 else 0,
                0,
                BigDecimal(dialog_Record_Amount.text.toString()),
                Instant.now().toEpochMilli()
        ))

        val accountModel by viewModels<AccountViewModel>()
        accountModel.getAccountById((dialog_Record_Account.tag as Account).id!!).observe(viewLifecycleOwner, Observer {
            val tempAccount = it.account
            val recordValue = BigDecimal(dialog_Record_Amount.text.toString())

            tempAccount.currentValue = tempAccount.currentValue!!.add(recordValue)

            accountModel.getAccountById((dialog_Record_Account.tag as Account).id!!).removeObservers(viewLifecycleOwner)
            accountModel.updateAccount(tempAccount)
        })

        setNavigationResult("record", true)

        dismiss()
    }
}