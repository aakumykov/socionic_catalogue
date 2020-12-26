package ru.aakumykov.me.sociocat.preferences2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import ru.aakumykov.me.sociocat.singletons.AuthSingleton;

public class Preferences2_Fragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private final static String PER_USER_PREFS_KEY_DELIMITER = "__";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        String currentUserId = AuthSingleton.currentUserId();
        if (null == currentUserId)
            return;

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());

        // Категория "Уведомления"
        PreferenceCategory categoryNotifications = createPreferenceCategory(
                "Уведомления",
                preferenceScreen
        );
        preferenceScreen.addPreference(categoryNotifications);

        // Переключатель "Карточки"
        String cardsNotificationsKey = "notify_about_new_cards";
        SwitchPreference cardsSwitch = createSwitchPreference(
                cardsNotificationsKey,
                "О новых карточках",
                "Уведомлять о новых карточках",
                false
        );
        categoryNotifications.addPreference(cardsSwitch);


        // Переключатель "Комментарии"
        String commentsNotificationsKey = "notify_about_new_comments";
        SwitchPreference commentsSwitch = createSwitchPreference(
                commentsNotificationsKey,
                "О новых комментариях",
                "Уведомлять о новых комментариях",
                false
        );
        categoryNotifications.addPreference(commentsSwitch);

        // Отображаю настройки
        setPreferenceScreen(preferenceScreen);

        // Настраиваю зависимости
        commentsSwitch.setDependency(cardsNotificationsKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    private PreferenceCategory createPreferenceCategory(String title, PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setIconSpaceReserved(false);
        preferenceCategory.setTitle(title);
        preferenceScreen.addPreference(preferenceCategory);
        return preferenceCategory;
    }

    private SwitchPreference createSwitchPreference(String key, String title, String subtitle, boolean defaultValue) {
        SwitchPreference switchPreference = new SwitchPreference(getContext());
        switchPreference.setTitle(title);
        switchPreference.setSummary(subtitle);
        switchPreference.setKey(key);
        switchPreference.setDefaultValue(defaultValue);
        switchPreference.setIconSpaceReserved(false);
        return switchPreference;
    }
}
