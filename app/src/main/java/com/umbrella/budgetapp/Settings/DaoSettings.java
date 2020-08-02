package com.umbrella.budgetapp.Settings;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.umbrella.budgetapp.Extensions.App;

public class DaoSettings extends PreferenceDataStore {

    @Override
    public void putString(String key, @Nullable String value) {
        App.getInstance().getUserDocument().update(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        App.getInstance().getUserDocument().update(key, value);
    }
}
