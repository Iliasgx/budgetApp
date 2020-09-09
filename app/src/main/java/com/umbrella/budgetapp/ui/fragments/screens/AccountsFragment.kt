package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.adapters.AccountsAdapter
import com.umbrella.budgetapp.adapters.BaseAdapter.CallBack
import com.umbrella.budgetapp.database.viewmodels.AccountViewModel
import com.umbrella.budgetapp.databinding.FragmentRecyclerViewBinding
import com.umbrella.budgetapp.extensions.fix
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class AccountsFragment : ExtendedFragment(R.layout.fragment_recycler_view) {
    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)

    private lateinit var adapter: AccountsAdapter

    val model by viewModels<AccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getAllAccounts().observe(viewLifecycleOwner, Observer { adapter.setData(it) })

        setUpRecyclerView()

        binding.fragmentFloatingActionButton.setOnClickListener {
            findNavController().navigate(AccountsFragmentDirections.accountsToUpdateAccount())
        }
    }

    private fun setUpRecyclerView() {
        adapter = AccountsAdapter(object : CallBack {
            override fun onItemClick(itemId: Long) {
                findNavController().navigate(AccountsFragmentDirections.accountsToUpdateAccount(itemId))
            }
        })

        val manager = DragManageAdapter(UP.or(DOWN), -1,  object: DragManageAdapter.OnAction {
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