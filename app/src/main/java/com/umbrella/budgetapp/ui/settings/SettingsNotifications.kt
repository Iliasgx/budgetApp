package com.umbrella.budgetapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.extensions.onSwitch

class SettingsNotifications: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_notifications, rootKey)
        setExtraListeners()
        setSwitches()
    }

    private fun setExtraListeners() {
        // If User doesn't allow notifications, all notifications need to be disabled and turned of,
        // otherwise they need to be enabled.
        findPreference<SwitchPreference>(getString(R.string.preferences_Key_GeneralNotifications))?.setOnPreferenceChangeListener { it, newValue ->
            val screen = findPreference<PreferenceScreen>(getString(R.string.preferences_Key_Screen_Notifications))
                    ?: return@setOnPreferenceChangeListener false

            // Loop through all mainPreferences in the screen.
            for (index in 0 until screen.preferenceCount) {
                val item = screen.getPreference(index)

                // If the item at index X is a PreferenceCategory, we have to loop through their children because
                // the main loop can't access the grandchildren
                if (item is PreferenceCategory) {
                    // Loop through all Preferences of that Category.
                    for (innerIndex in 0 until item.preferenceCount) {
                        val innerItem = item.getPreference(innerIndex)

                        // If it's a Switch and not turned on (& is not the Switch we used to active this) then set unchecked.
                        if (innerItem is SwitchPreference && item != it && !(newValue as Boolean)) {
                            innerItem.isChecked = false
                        }
                    }
                }
            }
            return@setOnPreferenceChangeListener true
        }
    }

    private fun setSwitches() {
        // TODO: 15/09/2020 Add functions for switches
        onSwitch(R.string.preferences_Key_Notifications_Statusbar) {}
    }
}