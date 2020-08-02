package com.umbrella.budgetapp.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.umbrella.budgetapp.MainActivity;
import com.umbrella.budgetapp.R;

import java.util.Objects;

public class SettingsDefault extends PreferenceFragmentCompat {
    private DaoSettings settings = new DaoSettings();

    private SwitchPreference mSecurityPin;
    private SwitchPreference mSecurityFingerprint;
    private SwitchPreference mDarkTheme;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        getPreferenceManager().setPreferenceDataStore(settings);

        mSecurityPin = listener(getString(R.string.preferences_Key_Security_Pin));
        mSecurityFingerprint = listener(getString(R.string.preferences_Key_Security_Fingerprint));
        mDarkTheme = listener(getString(R.string.preferences_Key_DarkTheme));

        listener(getString(R.string.preferences_Key_Group_userProfileSettings)).setOnPreferenceClickListener(pref -> navigate(SettingsDefaultDirections.globalUserProfile()));
        listener(getString(R.string.preferences_Key_Group_Notifications)).setOnPreferenceClickListener(pref -> navigate(SettingsDefaultDirections.settingsDefaultToSettingsNotifications()));
        listener(getString(R.string.preferences_Key_Group_Lists)).setOnPreferenceClickListener(pref -> navigate(SettingsDefaultDirections.settingsDefaultToSettingsLists()));
        listener(getString(R.string.preferences_Key_Group_About)).setOnPreferenceClickListener(pref -> navigate(SettingsDefaultDirections.globalAbout()));

        mSecurityPin.setOnPreferenceChangeListener(((preference, newValue) -> {
            settings.putBoolean(preference.getKey(), (boolean)newValue);
            if (!(boolean)newValue) ((SwitchPreference)listener(getString(R.string.preferences_Key_Security_Fingerprint))).setChecked(false);
            return true;
        }));
        mSecurityFingerprint.setOnPreferenceChangeListener(((preference, newValue) -> {
            settings.putBoolean(preference.getKey(), (boolean)newValue);
            return true;
        }));
        mDarkTheme.setOnPreferenceChangeListener(((preference, newValue) -> {
            // STOPSHIP: Still in development
            AppCompatDelegate.setDefaultNightMode((boolean)newValue ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            startActivity(new Intent(requireActivity().getApplicationContext(), MainActivity.class));
            requireActivity().finish();
            return true;
        }));

        setValues();
        Log.d("_Test", "[SettingsDefault] onCreatePreferences() ");
    }

    private boolean navigate(@NonNull NavDirections direction) {
        Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(direction);
        return true;
    }

    @NonNull
    private <T extends Preference> T listener(String key) {
        return Objects.requireNonNull(findPreference(key));
    }

    private void setValues() {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        mSecurityPin.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Security_Pin), false));
        mSecurityFingerprint.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Security_Fingerprint), false));
        mDarkTheme.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_DarkTheme), false));
    }
}
