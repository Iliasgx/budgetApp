package com.umbrella.budgetapp.extensions

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.adapters.BaseAdapter
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

fun RecyclerView.fix(adapter: BaseAdapter<*>, manager: ExtendedFragment.DragManageAdapter? = null) {
    layoutManager = LinearLayoutManager(context)
    setAdapter(adapter)
    setHasFixedSize(true)

    if (manager != null) {
        ItemTouchHelper(manager).attachToRecyclerView(this)
    }
}