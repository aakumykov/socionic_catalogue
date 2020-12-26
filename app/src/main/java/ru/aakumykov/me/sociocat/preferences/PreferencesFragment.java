package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.NotificationConstants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.push_notifications.PushSubscription_Helper;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.NotificationsHelper;

public class PreferencesFragment
        extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = "PreferencesFragment";
    private static final String PREFERENCE_KEY_DELIMITER = "__"; // Если поменять, старые настройки станут недоступны!
    private SharedPreferences mSharedPreferences;
    private String mCurrentUserId;

    // PreferencesFragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mSharedPreferences = getPreferenceScreen().getSharedPreferences();
        mCurrentUserId = AuthSingleton.currentUserId();

        configureBackupCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case Constants.PREFERENCE_KEY_notify_about_new_cards:
                processNewCardsNotification(key);
                break;

            case Constants.PREFERENCE_KEY_notify_on_comments:
                processCommentsNotification(key);
                break;

            default:
                Log.e(TAG, "Unknown preferences key '"+key+"'");
                break;
        }
    }


    // Внутренние методы
    private void configureBackupCategory() {
        if (!UsersSingleton.getInstance().currentUserIsAdmin()) {
            String categoryName = getString(R.string.PREFERENCES_category_backup_key);
            PreferenceCategory category = (PreferenceCategory) findPreference(categoryName);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removePreference(category);
        }
    }


    private void processNewCardsNotification(String preferencesKey) {

        if (null == mCurrentUserId)
            return;

        String keyReal = preferencesKey + PREFERENCE_KEY_DELIMITER + mCurrentUserId;
        boolean enabled = mSharedPreferences.getBoolean(keyReal, false);

//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean()

        if (enabled) {
            NotificationsHelper.createNotificationChannel(
                    getActivity(),
                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS,
                    R.string.NOTIFICATIONS_new_cards_channel_title,
                    R.string.NOTIFICATIONS_new_cards_channel_description,
                    new NotificationsHelper.NotificationChannelCreationCallbacks() {
                        @Override
                        public void onNotificationChannelCreateSuccess() {
                            subscribe2topic(
                                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS,
                                    preferencesKey,
                                    R.string.PREFERENCES_subscription_on_new_cards_success,
                                    R.string.PREFERENCES_subscription_on_new_cards_failed
                            );
                        }

                        @Override
                        public void onNotificationChannelCreateError(String errorMsg) {
                            revertPreferencesKey(preferencesKey, false);
                        }
                    }
            );
        }
        else {
            unsubscribe4romTopic(
                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS,
                    preferencesKey,
                    R.string.PREFERENCES_unsubscribe_from_new_cards_success,
                    R.string.PREFERENCES_unsubscribe_from_new_cards_failed
            );
        }
    }

    private void processCommentsNotification(String preferencesKey) {

        String userId = AuthSingleton.currentUserId();
        if (null == userId)
            return;

        boolean enabled = mSharedPreferences.getBoolean(preferencesKey, false);

        if (enabled) {
            NotificationsHelper.createNotificationChannel(
                    getActivity(),
                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS,
                    R.string.NOTIFICATIONS_new_comments_channel_title,
                    R.string.NOTIFICATIONS_new_comments_channel_description,
                    new NotificationsHelper.NotificationChannelCreationCallbacks() {
                        @Override
                        public void onNotificationChannelCreateSuccess() {
                            subscribe2topic(
                                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS,
                                    preferencesKey,
                                    R.string.PREFERENCES_subscription_on_new_comments_success,
                                    R.string.PREFERENCES_subscription_on_new_comments_failed
                            );
                        }

                        @Override
                        public void onNotificationChannelCreateError(String errorMsg) {
                            revertPreferencesKey(preferencesKey, false);
                        }
                    }
            );
        }
        else {
            unsubscribe4romTopic(
                    NotificationConstants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS,
                    preferencesKey,
                    R.string.PREFERENCES_unsubscribe_from_new_comments_success,
                    R.string.PREFERENCES_unsubscribe_from_new_comments_failed
            );
        }
    }


    private void subscribe2topic(String topicName, String preferencesKey, int successMessageId, int errorMessageId) {
        PushSubscription_Helper.subscribe2topic(topicName, new PushSubscription_Helper.SubscriptionCallbacks() {
            @Override
            public void onSubscribeSuccess() {
                showToast(successMessageId);
                savePreferenceBooleanForCurrentUser(preferencesKey, true);
            }

            @Override
            public void onSubscribeError(String errorMsg) {
                revertPreferencesKey(preferencesKey, false);
                showToast(errorMessageId);
            }
        });
    }

    private void unsubscribe4romTopic(String topicName, String preferencesKey, int successMsgId, int errorMsgId) {
        PushSubscription_Helper.unsubscribeFromTopic(topicName, new PushSubscription_Helper.UnsubscriptionCallbacks() {
            @Override
            public void onUnsubscribeSuccess() {
                showToast(successMsgId);
                savePreferenceBooleanForCurrentUser(preferencesKey, false);
            }

            @Override
            public void onUnsubscribeError(String errorMsg) {
                revertPreferencesKey(preferencesKey, true);
                showToast(errorMsgId);
            }
        });
    }

    private void savePreferenceBooleanForCurrentUser(String key, boolean booleanValue) {
        String personalizedKey = key + PREFERENCE_KEY_DELIMITER + mCurrentUserId;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(personalizedKey, booleanValue);
        editor.commit();
    }


    private void revertPreferencesKey(String key, boolean toValue) {
        mSharedPreferences.edit().putBoolean(key, toValue).apply();
        showToast(R.string.PREFERENCES_error_changing_preference);
    }

    private void showToast(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

}
