package com.umbrella.budgetapp.extensions

import androidx.annotation.StringRes
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

// Function for handling clickEvent Navigation with simple Preference.
fun PreferenceFragmentCompat.onNavAction(@StringRes resId: Int, direction: NavDirections) {
    findPreference<Preference>(getString(resId))?.setOnPreferenceClickListener {
        findNavController().navigate(direction)
        return@setOnPreferenceClickListener true
    }
}

fun PreferenceFragmentCompat.onSwitch(@StringRes resId: Int, result : (onResult : Boolean) -> Unit) {
    findPreference<SwitchPreference>(getString(resId))?.setOnPreferenceChangeListener { _, newValue ->
        result(newValue as Boolean)
        return@setOnPreferenceChangeListener true
    }
}