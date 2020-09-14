package com.umbrella.budgetapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.extensions.onNavAction

class SettingsLists : PreferenceFragmentCompat() {

    private val navDir = SettingsListsDirections

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_lists, rootKey)
        setExtraListeners()
    }

    private fun setExtraListeners() {
        onNavAction(R.string.preferences_Key_Group_Lists_Accounts, navDir.settingsListsToAccounts())
        onNavAction(R.string.preferences_Key_Group_Lists_Categories, navDir.settingsListsToCategories())
        onNavAction(R.string.preferences_Key_Group_Lists_Currencies, navDir.settingsListsToCurrencies())
        onNavAction(R.string.preferences_Key_Group_Lists_Templates, navDir.settingsListsToTemplates())
        onNavAction(R.string.preferences_Key_Group_Lists_Stores, navDir.settingsListsToStores())
    }
}