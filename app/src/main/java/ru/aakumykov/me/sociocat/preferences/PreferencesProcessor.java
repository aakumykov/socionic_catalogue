package ru.aakumykov.me.sociocat.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class PreferencesProcessor {

    public static void processAllPreferences(Context context, SharedPreferences sharedPreferences) {

        for (Map.Entry<String,?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            processPreferenceKey(context, sharedPreferences, key);
        }
    }

    static void processPreferenceKey(Context context, SharedPreferences sharedPreferences, String key) {

        String notifyOnNewCards = context.getString(R.string.PREFERENCES_notify_on_new_cards);

        if (notifyOnNewCards.equals(key)) {
            boolean enabled = sharedPreferences.getBoolean(key, true);
            processNewCardsSubscription(context, enabled);
        }

    }

    private static void processNewCardsSubscription(Context context, boolean isEnabled) {
        if (isEnabled)
            MVPUtils.subscribeToTopicNotifications(
                    context,
                    Constants.TOPIC_NEW_CARDS,
                    R.string.PREFERENCES_you_are_subscribed_to_new_cards,
                    R.string.PREFERENCES_error_subscribing_to_new_cards
            );
        else
            MVPUtils.unsubscribeFromTopicNotifications(
                    context,
                    Constants.TOPIC_NEW_CARDS,
                    R.string.PREFERENCES_you_are_unsubscribed_from_new_cards,
                    R.string.PREFERENCES_error_unsubscribing_from_new_cards_notifications
            );
    }
}
