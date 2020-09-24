package com.umbrella.budgetapp.ui.fragments.editors

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Category
import com.umbrella.budgetapp.database.viewmodels.CategoryViewModel
import com.umbrella.budgetapp.databinding.DataCategoryBinding
import com.umbrella.budgetapp.extensions.afterTextChangedDelayed
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import com.umbrella.budgetapp.ui.customs.Spinners
import com.umbrella.budgetapp.ui.customs.Spinners.Colors.Size
import com.umbrella.budgetapp.ui.interfaces.Edit
import com.umbrella.budgetapp.ui.interfaces.Edit.Type

class UpdateCategoryFragment: ExtendedFragment(R.layout.data_category), Edit {
    private val binding by viewBinding(DataCategoryBinding::bind)

    private companion object {
        const val MIN_NAME_LENGTH = 5
    }

    val args : UpdateCategoryFragmentArgs by navArgs()

    private val model by viewModels<CategoryViewModel>()

    private lateinit var category: Category

    private lateinit var type: Type

    private var editData = Category()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = checkType(args.categoryId)

        setToolbar(ToolBarNavIcon.CANCEL)
        setTitle(when (type) {
            Type.NEW -> R.string.title_add_category
            Type.EDIT -> R.string.title_edit_category
        })

        model.getCategoryById(args.categoryId).observe(viewLifecycleOwner, Observer {
            if (type == Type.EDIT) {
                category = it
                editData = category.copy()
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
            Spinners.Colors(this@UpdateCategoryFragment, dataCardCategoryColor, Size.SMALL)
            Spinners.Icons(this@UpdateCategoryFragment, dataCardCategoryIcon)

            if (type == Type.EDIT) {
                dataCardCategoryName.setText(category.name, EDITABLE)
                // TODO: 15/09/2020 Find out how to getCurrent, arrays don't work
                //dataCardCategoryColor.setSelection(category.color!!)
                //dataCardCategoryIcon.setSelection(category.icon!!)
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
            dataCardCategoryName.afterTextChangedDelayed { editData.name = it }

            dataCardCategoryColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    dataCardCategoryImage.backgroundTintList = ColorStateList.valueOf(resources.getIntArray(R.array.colors)[position])
                    editData.color = position
                }
            }

            dataCardCategoryIcon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    resources.obtainTypedArray(R.array.icons).apply {
                        val resourceId = getResourceId(position, 0)

                        dataCardCategoryImage.setImageResource(resourceId)
                        editData.icon = resourceId
                        recycle()
                    }
                }
            }

            //Automatically call the listeners so the Image has the proper Icon and Color after setting.
            dataCardCategoryColor.setSelection(dataCardCategoryColor.selectedItemPosition)
            dataCardCategoryIcon.setSelection(dataCardCategoryIcon.selectedItemPosition)
        }
    }

    /**
     * Check if all requirements are met before saving the data.
     */
    override fun checkData() {
        var onNext = false

        with(binding.dataCardCategoryName) {
            text.trim()
            when {
                text.isBlank() -> error = getString(R.string.data_Category_ErrorMsg_empty)
                text.length < MIN_NAME_LENGTH -> error = getString(R.string.data_Category_ErrorMsg_tooShort, MIN_NAME_LENGTH)
                else -> onNext = true
            }
        }

        //If requirements are not met, do not continue with updating data.
        if (!onNext) return

        //All requirements are met and all data is loaded. Ready to update data or create new one.
        saveData()
    }

    /**
     * Save all data after the requirements are checked.
     */
    override fun saveData() {
        if (type == Type.NEW) {
            model.addCategory(editData)
        } else if (hasChanges(category, editData)) {
            model.updateCategory(editData)
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