package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.FragmentHomeInformationBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment

class HomeInformationFragment : ExtendedFragment(R.layout.fragment_home_information) {
    private val binding by viewBinding(FragmentHomeInformationBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}