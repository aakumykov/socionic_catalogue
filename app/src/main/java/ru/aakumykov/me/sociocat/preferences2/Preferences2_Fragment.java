package ru.aakumykov.me.sociocat.preferences2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class Preferences2_Fragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private final static String PER_USER_PREFS_KEY_DELIMITER = "__";
    private PreferenceScreen mPreferenceScreen;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        mPreferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());

        String currentUserId = AuthSingleton.currentUserId();
        boolean userIsAdmin = UsersSingleton.getInstance().currentUserIsAdmin();

        if (null == currentUserId)
            return;

        createNotificationsPreferences();
        createBackupPreferences();

        setPreferenceScreen(mPreferenceScreen);

//        dropboxAccessTokenEditText.setDependency(performBackupKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    private void createBackupPreferences() {
        // ==== Категория "Резервное копирование" ====
        PreferenceCategory categoryBackup = createPreferenceCategory(
                "Резервное копирование",
                mPreferenceScreen
        );
        mPreferenceScreen.addPreference(categoryBackup);


        // Выполнять резервное копирование
        String performBackupKey = "perform_backup";
        SwitchPreference performBackupSwitch = createSwitchPreference(
                performBackupKey,
                "Базы данных",
                "Выполнять резервное копирование базы данных на сервер DropBox",
                false
        );
        categoryBackup.addPreference(performBackupSwitch);


        // Ключ доступа Dropbox
        String dropboxAccessTokenKey = "dropbox_api_key";
        EditTextPreference dropboxAccessTokenEditText = createEditTextPreference(
                dropboxAccessTokenKey,
                R.string.PREFERENCE_dropbox_access_token_title,
                R.string.PREFERENCE_dropbox_access_token_description,
                null
        );
        categoryBackup.addPreference(dropboxAccessTokenEditText);
    }

    private void createNotificationsPreferences() {

        // ==== Категория "Уведомления" ====
        PreferenceCategory categoryNotifications = createPreferenceCategory(
                "Уведомления",
                mPreferenceScreen
        );
        mPreferenceScreen.addPreference(categoryNotifications);


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

    private EditTextPreference createEditTextPreference(String key, int titleId, int summaryId, @NonNull String defaultValue) {
        EditTextPreference editTextPreference = new EditTextPreference(getContext());
        editTextPreference.setKey(key);
        editTextPreference.setTitle(titleId);
        editTextPreference.setSummary(summaryId);
        editTextPreference.setIconSpaceReserved(false);
        editTextPreference.setDialogTitle(titleId);
        return editTextPreference;
    }
}