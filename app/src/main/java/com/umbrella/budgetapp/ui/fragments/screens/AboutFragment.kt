package com.umbrella.budgetapp.ui.fragments.screens

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.umbrella.budgetapp.BuildConfig
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.databinding.FragmentAboutBinding
import com.umbrella.budgetapp.ui.customs.ExtendedFragment
import java.time.Year

class AboutFragment : ExtendedFragment(R.layout.fragment_about) {
    private val binding by viewBinding(FragmentAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            aboutAppVersion.text = BuildConfig.VERSION_NAME
            aboutCopyright.text = getString(R.string.copyright, Year.now().value)

            val coders = resources.getStringArray(R.array.developersNames)
            val authors = resources.getStringArray(R.array.basedOnNames)

            aboutDevelopersList.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, coders)
            aboutNamesList.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, authors)
        }
    }
}