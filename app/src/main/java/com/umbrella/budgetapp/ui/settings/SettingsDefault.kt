package com.umbrella.budgetapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.extensions.onNavAction
import com.umbrella.budgetapp.extensions.onSwitch

class SettingsDefault : PreferenceFragmentCompat() {

    private val navDir = SettingsDefaultDirections

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setExtraListeners()
        setSwitches()
    }

    private fun setExtraListeners() {
        // Fingerprint shouldn't be enabled when security 'pin' is turned off.
        findPreference<SwitchPreference>(getString(R.string.preferences_Key_Security_Pin))?.setOnPreferenceChangeListener { _, newValue ->
            if (!(newValue as Boolean)) findPreference<SwitchPreference>(getString(R.string.preferences_Key_Security_Fingerprint))?.isChecked = false
            return@setOnPreferenceChangeListener true
        }

        onNavAction(R.string.preferences_Key_Group_userProfileSettings, navDir.settingsDefaultToUpdateUserProfile(userId = Memory.loggedUser.id ?: 0L))
        onNavAction(R.string.preferences_Key_Group_Notifications, navDir.settingsDefaultToSettingsNotifications())
        onNavAction(R.string.preferences_Key_Group_Lists, navDir.settingsDefaultToSettingsLists())
        onNavAction(R.string.preferences_Key_Group_About, navDir.globalAbout())
    }

    private fun setSwitches() {
        // TODO: 15/09/2020 Add functions for switches
        onSwitch(R.string.preferences_Key_Language) {}
    }
}