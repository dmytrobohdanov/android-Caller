package com.dmytro.caller;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        addPreferencesFromResource(R.xml.pref_general);

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;

    }
}
