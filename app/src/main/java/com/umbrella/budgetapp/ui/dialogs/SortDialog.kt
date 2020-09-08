package com.umbrella.budgetapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.enums.SortType
import com.umbrella.budgetapp.enums.SortType.DEFAULT
import com.umbrella.budgetapp.extensions.setNavigationResult
import kotlinx.android.synthetic.main.dialog_sort.*

class SortDialog : DialogFragment() {

    private val args by navArgs<SortDialogArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check the item of the arguments. When default, none should be selected
        if (args.currentFilter != DEFAULT) {
            (dialog_Sort_RadioGroup.getChildAt(args.currentFilter.ordinal - 1) as RadioButton).isChecked = true
        }

        dialog_Sort_RadioGroup.setOnCheckedChangeListener { it, checkedId ->
             returnType(SortType.values()[it.indexOfChild(it.findViewById(checkedId)) + 1])
        }

        dialog_Sort_Default.setOnClickListener { returnType(DEFAULT) }

        dialog_Sort_Cancel.setOnClickListener { dismiss() }
    }

    private fun returnType(sortType: SortType) {
        setNavigationResult("type", sortType)
    }
}