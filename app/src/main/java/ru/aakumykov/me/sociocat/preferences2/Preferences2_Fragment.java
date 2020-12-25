package ru.aakumykov.me.sociocat.preferences2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;

public class Preferences2_Fragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        String currentUserId = AuthSingleton.currentUserId();
        if (null != currentUserId) {
            String cardsNotificationsPrefKey = Constants.PREFERENCE_KEY_notify_about_new_cards + "__" + currentUserId;

            PreferenceManager preferenceManager = getPreferenceManager();
            PreferenceScreen preferenceScreen = preferenceManager.createPreferenceScreen(getContext());

            PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
            preferenceCategory.setTitle(R.string.PREFERENCE_category_notifications);

            preferenceScreen.addPreference(preferenceCategory);

            SwitchPreference switchPreference = new SwitchPreference(getContext());
            switchPreference.setTitle(R.string.PREFERENCE_notify_about_new_cards_title);
            switchPreference.setSummary(R.string.PREFERENCE_notify_about_new_cards_description);
            switchPreference.setKey(cardsNotificationsPrefKey);
            switchPreference.setDefaultValue(false);

            preferenceCategory.addPreference(switchPreference);

            setPreferenceScreen(preferenceScreen);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
