package ru.aakumykov.me.sociocat.preferences2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import ru.aakumykov.me.sociocat.R;

public class Preferences2_Fragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        PreferenceManager preferenceManager = getPreferenceManager();
//        PreferenceScreen preferenceScreen = preferenceManager.getPreferenceScreen();
        PreferenceScreen preferenceScreen = preferenceManager.createPreferenceScreen(getContext());

        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setTitle(R.string.PREFERENCE_category_notifications);

        Preference preference = new Preference(getContext());
        preference.setTitle(R.string.PREFERENCE_notify_about_new_cards_title);
        preference.setSummary(R.string.PREFERENCE_notify_about_new_cards_description);
        preference.setKey("nonc");
        preference.setDefaultValue(false);
        preference.setEnabled(true);

        preferenceCategory.addPreference(preference);

        preferenceScreen.addPreference(preferenceCategory);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
