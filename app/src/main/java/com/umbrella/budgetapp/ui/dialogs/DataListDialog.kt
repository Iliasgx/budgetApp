package com.umbrella.budgetapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.DataListAdapter
import com.umbrella.budgetapp.adapters.DataListAdapter.DataCallBack
import com.umbrella.budgetapp.database.viewmodels.*
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.extensions.setNavigationResult
import kotlinx.android.synthetic.main.dialog_recycler_view.*

class DataListDialog : DialogFragment() {

    private val args by navArgs<DataListDialogArgs>()

    enum class DataLocationType {
        CURRENCY,
        CATEGORY,
        ACCOUNT,
        TEMPLATE,
        STORE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (args.type) {
            DataLocationType.CURRENCY -> {
                val model by viewModels<CurrencyViewModel>()
                initAdapter(model.getAllCurrencies())
            }
            DataLocationType.CATEGORY -> {
                val model by viewModels<CategoryViewModel>()
                initAdapter(model.getAllCategories())
            }
            DataLocationType.ACCOUNT -> {
                val model by viewModels<AccountViewModel>()
                initAdapter(model.getAllAccounts())
            }
            DataLocationType.TEMPLATE -> {
                val model by viewModels<TemplateViewModel>()
                initAdapter(model.getAllTemplates())
            }
            DataLocationType.STORE -> {
                val model by viewModels<StoreViewModel>()
                initAdapter(model.getAllStores())
            }
        }
    }

    private fun <T> initAdapter(data: LiveData<List<T>>) {
        val adapter = DataListAdapter<T>(object : DataCallBack<T> {
            override fun onItemSelected(item: T) {
                setNavigationResult("data", item)
                dismiss()
            }
        })

        data.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        })

        dialog_recyclerView.fix(adapter)
    }
}