package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import androidx.annotation.Nullable;
import ru.aakumykov.me.sociocat.R;

public class PreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.PREFERENCES_notify_on_new_cards))) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference(key);
            if (switchPreference.isChecked()) {
                switchPreference.setSummary(getString(R.string.PREFERENCES_notify_on_new_cards_enabled_summary));
            } else {
                switchPreference.setSummary(getString(R.string.PREFERENCES_notify_on_new_cards_disabled_summary));
            }
        }
    }

}
