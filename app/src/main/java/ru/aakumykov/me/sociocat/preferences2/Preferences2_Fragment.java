package ru.aakumykov.me.sociocat.preferences2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

    public Preferences2_Fragment() {
    }

    public void assemblePreferences() {
        if (null != mPreferenceScreen)
        {
            String currentUserId = AuthSingleton.currentUserId();

            if (null == currentUserId)
                return;

            createNotificationsPreferences();

            if (UsersSingleton.getInstance().currentUserIsAdmin())
                createBackupPreferences();
        }
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mPreferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
        setPreferenceScreen(mPreferenceScreen);

        assemblePreferences();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    private void createNotificationsPreferences() {

        // ==== Категория "Уведомления" ====
        PreferenceCategory categoryNotifications = createPreferenceCategory(
                R.string.PREFERENCES_category_name_notifications,
                mPreferenceScreen
        );
        mPreferenceScreen.addPreference(categoryNotifications);


        // Переключатель "Карточки"
        String cardsNotificationsKey = getString(R.string.PREFERENCES_notify_on_new_cards_key);
        SwitchPreference cardsSwitch = createSwitchPreference(
                cardsNotificationsKey,
                R.string.PREFERENCES_notify_on_new_cards_title,
                R.string.PREFERENCES_notify_on_new_cards_description,
                false
        );
        categoryNotifications.addPreference(cardsSwitch);


        // Переключатель "Комментарии"
        String commentsNotificationsKey = getString(R.string.PREFERENCES_notify_on_new_comments_key);
        SwitchPreference commentsSwitch = createSwitchPreference(
                commentsNotificationsKey,
                R.string.PREFERENCES_notify_on_new_comments_title,
                R.string.PREFERENCES_notify_on_new_comments_description,
                false
        );
        categoryNotifications.addPreference(commentsSwitch);
    }

    private void createBackupPreferences() {
        // ==== Категория "Резервное копирование" ====
        PreferenceCategory categoryBackup = createPreferenceCategory(
                R.string.PREFERENCES_category_name_backup,
                mPreferenceScreen
        );
        mPreferenceScreen.addPreference(categoryBackup);


        // Выполнять резервное копирование
        String performBackupKey = getString(R.string.PREFERENCES_perform_backup_key);
        SwitchPreference performBackupSwitch = createSwitchPreference(
                performBackupKey,
                R.string.PREFERENCES_perform_backup_title,
                R.string.PREFERENCES_perform_backup_description,
                false
        );
        categoryBackup.addPreference(performBackupSwitch);


        // Ключ доступа Dropbox
        String dropboxAccessTokenKey = getString(R.string.PREFERENCES_dropbox_access_token_key);
        EditTextPreference dropboxAccessTokenEditText = createEditTextPreference(
                dropboxAccessTokenKey,
                R.string.PREFERENCES_dropbox_access_token_title,
                R.string.PREFERENCES_dropbox_access_token_description,
                null
        );
        categoryBackup.addPreference(dropboxAccessTokenEditText);

        dropboxAccessTokenEditText.setDependency(performBackupKey);
    }


    private PreferenceCategory createPreferenceCategory(int title, PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setIconSpaceReserved(false);
        preferenceCategory.setTitle(title);
        preferenceScreen.addPreference(preferenceCategory);
        return preferenceCategory;
    }

    private SwitchPreference createSwitchPreference(String key, int title, int subtitle, boolean defaultValue) {
        SwitchPreference switchPreference = new SwitchPreference(getContext());
        switchPreference.setTitle(title);
        switchPreference.setSummary(subtitle);
        switchPreference.setKey(key);
        switchPreference.setDefaultValue(defaultValue);
        switchPreference.setIconSpaceReserved(false);
        return switchPreference;
    }

    private EditTextPreference createEditTextPreference(String key, int titleId, int summaryId, @Nullable String defaultValue) {
        EditTextPreference editTextPreference = new EditTextPreference(getContext());
        editTextPreference.setKey(key);
        editTextPreference.setTitle(titleId);
        editTextPreference.setSummary(summaryId);
        editTextPreference.setIconSpaceReserved(false);
        editTextPreference.setDialogTitle(titleId);

        if (null != defaultValue)
            editTextPreference.setDefaultValue(defaultValue);

        return editTextPreference;
    }


}
