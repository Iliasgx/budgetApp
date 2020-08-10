package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.LoginBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class LoginFragment : ExtendedFragment(R.layout._login) {
    private val binding by viewBinding(LoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}