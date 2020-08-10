package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.FragmentHomeAccountsBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class HomeAccountsFragment : ExtendedFragment(R.layout.fragment_home_accounts) {
    private val binding by viewBinding(FragmentHomeAccountsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}