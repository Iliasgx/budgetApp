package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.adapters.TemplatesAdapter
import com.umbrella.budgetapp.database.viewmodels.TemplateViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class TemplatesFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private val model by viewModels<TemplateViewModel>()

    private lateinit var adapter : TemplatesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllTemplates().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.setOnClickListener {
            findNavController().navigate(TemplatesFragmentDirections.templatesToUpdateTemplate())
        }
    }

    private fun setUpRecyclerView() {
        adapter = TemplatesAdapter(object : CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(TemplatesFragmentDirections.templatesToUpdateTemplate(itemId))
            }
        })

        val manager = DragManageAdapter(UP.or(DOWN), -1, object : DragManageAdapter.OnAction {
            override fun onMoved(itemId: Long, oldPosition: Int, newPosition: Int) {
                if (oldPosition != newPosition) {
                    model.changePosition(itemId, newPosition)

                    //Moved down
                    if (newPosition > oldPosition) {
                        model.decreasePositions(oldPosition, newPosition)
                    } else { // Moved up
                        model.increasePositions(newPosition, oldPosition)
                    }
                }
            }

            override fun onSwiped(itemId: Long) {}
        })

        binding.fragmentRecyclerView.fix(adapter, manager)
    }
}