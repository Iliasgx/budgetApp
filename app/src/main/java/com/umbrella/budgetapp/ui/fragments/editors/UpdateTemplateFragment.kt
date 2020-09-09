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
import com.umbrella.budgetapp.database.collections.Account
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.collections.subcollections.CurrencyAndName
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedStore
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
import com.umbrella.budgetapp.database.viewmodels.TemplateViewModel
import com.umbrella.budgetapp.databinding.DataTemplateBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.extensions.currencyText
import com.umbrella.budgetapp.extensions.getNavigationResult
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType.CATEGORY
import com.umbrella.budgetapp.ui.dialogs.DataListDialog.DataLocationType.STORE
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type
import java.math.BigDecimal

class UpdateTemplateFragment : ExtendedFragment(R.layout.data_template), Edit {
    private val binding by viewBinding(DataTemplateBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 4
        const val MIN_AMOUNT = 1.0f
    }

    val args : UpdateTemplateFragmentArgs by navArgs()

    private val model by viewModels<TemplateViewModel>()

    private lateinit var extTemplate : ExtendedTemplate

    private lateinit var type: Type

    private var editData = Template(id = 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.templateId)

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> R.string.title_add_template
            Type.EDIT -> R.string.title_edit_template
        })

        model.getTemplateById(args.templateId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                extTemplate = it
                editData = extTemplate.template
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
            Spinners.Currencies(this@UpdateTemplateFragment, dataCardTemplateCurrency)
            Spinners.Accounts(this@UpdateTemplateFragment, dataCardTemplateAccount)
            Spinners.SimpleSpinner(this@UpdateTemplateFragment, dataCardTemplateType, R.array.type)
            Spinners.SimpleSpinner(this@UpdateTemplateFragment, dataCardTemplatePayType, R.array.paymentType)

            if (type == Type.NEW) {
                dataCardTemplateAmount.currencyText("", BigDecimal.ZERO)
            } else {
                dataCardTemplateName.setText(extTemplate.template.name, EDITABLE)
                dataCardTemplateAmount.currencyText("", extTemplate.template.amount!!)
                dataCardTemplateCurrency.setSelection(extTemplate.currencyPosition!!)
                dataCardTemplateAccount.setSelection(extTemplate.accountPosition!!)
                dataCardTemplateCategory.text = extTemplate.category.name
                dataCardTemplateType.setSelection(extTemplate.template.type!!)
                dataCardTemplatePayType.setSelection(extTemplate.template.paymentType!!)
                dataCardTemplateStore.text = extTemplate.storeName
                dataCardTemplatePayee.setText(extTemplate.template.payee, EDITABLE)
                dataCardTemplateNote.setText(extTemplate.template.note, EDITABLE)
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
            dataCardTemplateName.afterTextChangedDelayed { editData.name = it }
            dataCardTemplatePayee.afterTextChangedDelayed { editData.payee = it }
            dataCardTemplateNote.afterTextChangedDelayed { editData.note = it }

            dataCardTemplateAmount.setOnClickListener {
                findNavController().navigate(UpdateTemplateFragmentDirections.globalDialogAmount(editData.amount?.toEngineeringString(), MIN_AMOUNT))
            }

            dataCardTemplateCategory.setOnClickListener {
                findNavController().navigate(UpdateTemplateFragmentDirections.globalDataListDialog(CATEGORY))

                getNavigationResult<Category>(R.id.updateTemplate, "data") { result ->
                    editData.categoryRef = result.id
                    dataCardTemplateCategory.text = result.name
                }
            }

            dataCardTemplateStore.setOnClickListener {
                findNavController().navigate(UpdateTemplateFragmentDirections.globalDataListDialog(STORE))

                getNavigationResult<ExtendedStore>(R.id.updateTemplate, "data") { result ->
                    editData.storeRef = result.store.id
                    dataCardTemplateStore.text = result.store.name
                }
            }

            getNavigationResult<String>(R.id.updateTemplate, "amount") { result ->
                editData.amount = BigDecimal(result)
                dataCardTemplateAmount.text = result
            }
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardTemplateName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Template_ErrorMsg_empty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Template_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //Update EditData with data outside listeners.
        with(binding) {
            editData.apply {
                currencyRef = (dataCardTemplateCurrency.selectedItem as CurrencyAndName).id
                accountRef = (dataCardTemplateAccount.selectedItem as Account).id
                type = dataCardTemplateType.selectedItemPosition
                paymentType = dataCardTemplatePayType.selectedItemPosition
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
            model.addTemplate(editData)
        } else if (hasChanges(extTemplate.template, editData)) {
            model.updateTemplate(editData)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLayout_SaveOnly) {
            checkData()
        } else {
            findNavController().navigateUp()
        }
        return true
    }
}