package com.umbrella.budgetapp.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.umbrella.budgetapp.R;

import java.util.Objects;

public class SettingsNotifications extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private DaoSettings settings = new DaoSettings();

    private SwitchPreference mGeneralNotifications;
    private SwitchPreference mStatusbar;
    private SwitchPreference mBanner;
    private SwitchPreference mVibrate;
    private SwitchPreference mPlannedPayments;
    private SwitchPreference mDebts;
    private SwitchPreference mGoals;
    private SwitchPreference mBudget;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_notifications, rootKey);

        getPreferenceManager().setPreferenceDataStore(settings);
        mGeneralNotifications = listener(getString(R.string.preferences_Key_GeneralNotifications));
        mStatusbar = listener(getString(R.string.preferences_Key_Notifications_Statusbar));
        mBanner = listener(getString(R.string.preferences_Key_Notifications_Banner));
        mVibrate = listener(getString(R.string.preferences_Key_Notifications_Vibrate));

        mPlannedPayments = listener(getString(R.string.preferences_Key_Notifications_PlannedPayments));
        mDebts = listener(getString(R.string.preferences_Key_Notifications_Debts));
        mGoals = listener(getString(R.string.preferences_Key_Notifications_Goals));
        mBudget = listener(getString(R.string.preferences_Key_Notifications_Budget));

        getPreferenceScreen().setOnPreferenceChangeListener(this);

        setValues();
        Log.d("_Test", "[SettingsNotifications] onCreatePreferences() ");
    }

    @NonNull
    private SwitchPreference listener(String key) {
        return Objects.requireNonNull(findPreference(key));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mGeneralNotifications) {
            if (!(boolean)newValue) {
                mStatusbar.setChecked(false);
                mBanner.setChecked(false);
                mVibrate.setChecked(false);

                mPlannedPayments.setChecked(false);
                mDebts.setChecked(false);
                mGoals.setChecked(false);
                mBudget.setChecked(false);
            }
            return true;
        }
        settings.putBoolean(preference.getKey(), (boolean)newValue);
        return true;
    }

    private void setValues() {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        mGeneralNotifications.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_GeneralNotifications), true));
        mStatusbar.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Statusbar), true));
        mBanner.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Banner), true));
        mVibrate.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Vibrate), true));

        mPlannedPayments.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_PlannedPayments), true));
        mDebts.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Debts), true));
        mGoals.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Goals), true));
        mBudget.setChecked(preferences.getBoolean(getString(R.string.preferences_Key_Notifications_Budget), true));

    }
}
