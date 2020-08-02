package com.umbrella.budgetapp.Settings;

import android.os.Bundle;
import android.util.Log;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.umbrella.budgetapp.R;

import java.util.Objects;

public class SettingsLists extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_lists, rootKey);

        navigateListener(getString(R.string.preferences_Key_Group_Lists_Accounts), SettingsListsDirections.settingsListsToAccounts());
        navigateListener(getString(R.string.preferences_Key_Group_Lists_Currencies), SettingsListsDirections.settingsListsToCurrencies());
        navigateListener(getString(R.string.preferences_Key_Group_Lists_Categories), SettingsListsDirections.settingsListsToCategories());
        navigateListener(getString(R.string.preferences_Key_Group_Lists_Templates), SettingsListsDirections.settingsListsToTemplates());
        navigateListener(getString(R.string.preferences_Key_Group_Lists_Stores), SettingsListsDirections.settingsListsToStores());
        navigateListener(getString(R.string.preferences_Key_Group_Lists_ShoppingItems), SettingsListsDirections.settingsListsToShoppingItems());

        Log.d("_Test", "[SettingsLists] onCreatePreferences() ");
    }

    private void navigateListener(String key, NavDirections directions) {
        ((Preference)Objects.requireNonNull(findPreference(key))).setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(directions);
            return true;
        });
    }
}
