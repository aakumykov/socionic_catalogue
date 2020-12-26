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
import ru.aakumykov.me.sociocat.constants.NotificationConstants;
import ru.aakumykov.me.sociocat.constants.PreferencesConstants;
import ru.aakumykov.me.sociocat.push_notifications.PUSHSubscriptionHelper;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.NotificationsHelper;

public class Preferences2_Fragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private final static String PER_USER_PREFS_KEY_DELIMITER = "__";
    private PreferenceScreen mPreferenceScreen;
    private SharedPreferences mSharedPreferences;

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

        mSharedPreferences = getPreferenceScreen().getSharedPreferences();

        assemblePreferences();
    }


    private void createNotificationsPreferences() {

        // ==== Категория "Уведомления" ====
        PreferenceCategory categoryNotifications = createPreferenceCategory(
                R.string.PREFERENCES_category_name_notifications,
                mPreferenceScreen
        );
        mPreferenceScreen.addPreference(categoryNotifications);


        // Переключатель "Карточки"
        String cardsNotificationsKey = PreferencesConstants.key_notify_on_new_cards;
        SwitchPreference cardsSwitch = createSwitchPreference(
                cardsNotificationsKey,
                R.string.PREFERENCES_notify_on_new_cards_title,
                R.string.PREFERENCES_notify_on_new_cards_description,
                false
        );
        categoryNotifications.addPreference(cardsSwitch);


        // Переключатель "Комментарии"
        String commentsNotificationsKey = PreferencesConstants.key_notify_on_new_comments;
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
        String performBackupKey = PreferencesConstants.key_perform_database_backup;
        SwitchPreference performBackupSwitch = createSwitchPreference(
                performBackupKey,
                R.string.PREFERENCES_perform_backup_title,
                R.string.PREFERENCES_perform_backup_description,
                false
        );
        categoryBackup.addPreference(performBackupSwitch);


        // Ключ доступа Dropbox
        String dropboxAccessTokenKey = PreferencesConstants.dropbox_access_token_key;
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



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case PreferencesConstants.key_notify_on_new_cards:
                processNewCardsNotificationPreference(key, sharedPreferences);
                break;
            case PreferencesConstants.key_notify_on_new_comments:
                processNewCommentsNotificationPreference(key, sharedPreferences);
                break;
            case PreferencesConstants.key_perform_database_backup:
                processPerformBackupPreference(key, sharedPreferences);
                break;
            default:
                break;
        }
    }


    private void processNewCardsNotificationPreference(String key, SharedPreferences sharedPreferences) {
        boolean isEnabled = sharedPreferences.getBoolean(key, false);

        if (isEnabled)
            subscribe2topic(NotificationConstants.NEW_CARDS_CHANNEL_NAME, new iSubscribe2topicCallbacks() {
                @Override
                public void onSubscriptionSuccess() {
                    showToast(R.string.PREFERENCES_subscription_on_new_cards_success);
                }

                @Override
                public void onSubscriptionFailed(String errorMsg) {
                    showToast(R.string.PREFERENCES_subscription_on_new_cards_failed);
                    revertPreferencesKey(key, false);
                }
            });
        else
            unsubscribeFromTopic(NotificationConstants.NEW_CARDS_CHANNEL_NAME);
    }

    private void processNewCommentsNotificationPreference(String key, SharedPreferences sharedPreferences) {
        boolean isEnabled = sharedPreferences.getBoolean(key, false);

        if (isEnabled)
            NotificationsHelper.
    }

    private void processPerformBackupPreference(String key, SharedPreferences sharedPreferences) {
        boolean value = sharedPreferences.getBoolean(key, false);

    }



    private void revertPreferencesKey(String key, boolean toValue) {
        mSharedPreferences.edit().putBoolean(key, toValue).apply();
        showToast(R.string.PREFERENCES_error_changing_preference);
    }

    private void showToast(int messageId) {
        MyUtils.showCustomToast(getContext(), messageId);
    }


    private void subscribe2topic(String channelName, iSubscribe2topicCallbacks callbacks) {
        NotificationsHelper.createNotificationChannel(
                getContext(),
                NotificationConstants.NEW_CARDS_CHANNEL_NAME,
                R.string.NOTIFICATIONS_new_cards_channel_title,
                R.string.NOTIFICATIONS_new_cards_channel_description,
                new NotificationsHelper.iNotificationChannelCreationCallbacks() {
                    @Override
                    public void onNotificationChannelCreateSuccess() {

                        PUSHSubscriptionHelper.subscribe2topic(
                                NotificationConstants.NEW_CARDS_CHANNEL_NAME,
                                new PUSHSubscriptionHelper.iSubscriptionCallbacks() {
                                    @Override
                                    public void onSubscribeSuccess() {
                                        callbacks.onSubscribeSuccess();
                                    }

                                    @Override
                                    public void onSubscribeError(String errorMsg) {
                                        callbacks.onSubscribeFailed(errorMsg);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onNotificationChannelCreateError(String errorMsg) {
                        callbacks.onSubscribeFailed(errorMsg);
                    }
                }
        );
    }

    private interface iSubscribe2topicCallbacks {
        void onSubscribeSuccess();
        void onSubscribeFailed(String errorMsg);
    }


    private void unsubscribeFromTopic(String channelName) {
        PUSHSubscriptionHelper.unsubscribeFromTopic(channelName, new PUSHSubscriptionHelper.iUnsubscriptionCallbacks() {
            @Override
            public void onUnsubscribeSuccess() {

            }

            @Override
            public void onUnsubscribeError(String errorMsg) {

            }
        });
    }

    private interface iUnscribeFromTopicCallbacks {
        void onUnsubscribeSuccess();
        void onUnsubscribeFailed(String errorMsg);
    }

}
