package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.adapters.CurrenciesAdapter
import com.umbrella.budgetapp.database.viewmodels.CurrencyViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrenciesFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private val model by viewModels<CurrencyViewModel>()

    private lateinit var adapter: CurrenciesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllCurrencies().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.apply {
            isVisible = true
            setOnClickListener {
                findNavController().navigate(CurrenciesFragmentDirections.currenciesToUpdateCurrency(nextPosition = adapter.itemCount))
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = CurrenciesAdapter(object : CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(CurrenciesFragmentDirections.currenciesToUpdateCurrency(itemId))
            }
        })

        val manager = DragManageAdapter(UP.or(DOWN), -1, object : DragManageAdapter.OnAction {
            override fun onMoved(itemId: Long, oldPosition: Int, newPosition: Int) {
                if (oldPosition != newPosition) {

                    //Checks if the first position is changed and updates this currency as a default.
                    if (oldPosition == 0) {
                        //Get the current first one of the list.
                        updateFirstCurrency(adapter.currencies.firstOrNull()?.id!!)
                    } else if (newPosition == 0) {
                        //Retrieves this item as it is the new first position item.
                        updateFirstCurrency(itemId)
                    }

                    model.changePosition(itemId, newPosition)

                    // Moved down
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

    fun updateFirstCurrency(currencyId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: 06/09/2020 save the first currency to apply as default.
        }
    }
}