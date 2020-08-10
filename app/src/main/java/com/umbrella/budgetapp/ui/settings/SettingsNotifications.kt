package com.umbrella.budgetapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.umbrella.budgetapp.R

class SettingsNotifications: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_notifications, rootKey)
    }
}