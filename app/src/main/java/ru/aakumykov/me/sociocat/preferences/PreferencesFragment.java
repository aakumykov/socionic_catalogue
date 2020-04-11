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
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.push_notifications.PushSubscription_Helper;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.NotificationsHelper;

public class PreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = "PreferencesFragment";
    private SharedPreferences sharedPreferences;

    // PreferencesFragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (!UsersSingleton.getInstance().currentUserIsAdmin()) {
            String categoryName = getString(R.string.PREFERENCE_category_backup_key);
            PreferenceCategory category = (PreferenceCategory) findPreference(categoryName);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removePreference(category);
        }
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


    // SharedPreferences.OnSharedPreferenceChangeListener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PreferencesProcessor.processPreferenceKey(getActivity(), sharedPreferences, key);

        switch (key) {
//            case "perform_database_backup":
//                MyUtils.showCustomToast(this.getActivity().getApplicationContext(), R.string.not_implemented_yet);
//                boolean enabled = sharedPreferences.getBoolean(key, false);
//                if (enabled) Backup_JobService.scheduleJob(getActivity());
//                else Backup_JobService.unscheduleJob(getActivity());
//                break;
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
    private void processNewCardsNotification(String preferencesKey) {
        boolean enabled = sharedPreferences.getBoolean(preferencesKey, false);

        if (enabled) {
            NotificationsHelper.createNotificationChannel(
                    getActivity(),
                    Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS,
                    R.string.NOTIFICATIONS_new_cards_channel_title,
                    R.string.NOTIFICATIONS_new_cards_channel_description,
                    new NotificationsHelper.NotificationChannelCreationCallbacks() {
                        @Override
                        public void onNotificationChannelCreateSuccess() {
                            subscribe2topic(Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS, preferencesKey);
                        }

                        @Override
                        public void onNotificationChannelCreateError(String errorMsg) {
                            revertPreferencesKey(preferencesKey, false);
                        }
                    }
            );
        }
        else {
            unsubscribe4romTopic(Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS, preferencesKey);
        }
    }

    private void processCommentsNotification(String preferencesKey) {
        boolean enabled = sharedPreferences.getBoolean(preferencesKey, false);

        if (enabled) {
            NotificationsHelper.createNotificationChannel(
                    getActivity(),
                    Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS,
                    R.string.NOTIFICATIONS_new_comments_channel_title,
                    R.string.NOTIFICATIONS_new_comments_channel_description,
                    new NotificationsHelper.NotificationChannelCreationCallbacks() {
                        @Override
                        public void onNotificationChannelCreateSuccess() {
                            subscribe2topic(Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS, preferencesKey);
                        }

                        @Override
                        public void onNotificationChannelCreateError(String errorMsg) {
                            revertPreferencesKey(preferencesKey, false);
                        }
                    }
            );
        }
        else {
            unsubscribe4romTopic(Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_CARDS, preferencesKey);
        }
    }


    private void subscribe2topic(String topicName, String preferencesKey) {
        PushSubscription_Helper.subscribe2topic(topicName, new PushSubscription_Helper.SubscriptionCallbacks() {
            @Override
            public void onSubscribeSuccess() {
                showToast(R.string.PREFERENCE_you_are_subscribed);
            }

            @Override
            public void onSubscribeError(String errorMsg) {
                revertPreferencesKey(preferencesKey, false);
            }
        });
    }

    private void unsubscribe4romTopic(String topicName, String preferencesKey) {
        PushSubscription_Helper.unsubscribeFromTopic(topicName, new PushSubscription_Helper.UnsubscriptionCallbacks() {
            @Override
            public void onUnsubscribeSuccess() {
                showToast(R.string.PREFERENCE_you_are_unsubscribed);
            }

            @Override
            public void onUnsubscribeError(String errorMsg) {
                revertPreferencesKey(preferencesKey, true);
            }
        });
    }


    private void revertPreferencesKey(String key, boolean toValue) {
        sharedPreferences.edit().putBoolean(key, toValue).apply();
        showToast(R.string.PREFERENCES_error_changing_preference);
    }

    private void showToast(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

}
