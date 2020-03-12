package ru.aakumykov.me.sociocat.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class PreferencesProcessor {

    // Внешние методы
    public static void processAllPreferences(Context context, SharedPreferences sharedPreferences) {

        for (Map.Entry<String,?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            processPreferenceKey(context, sharedPreferences, key);
        }
    }

    // Методы пакета
    static void processPreferenceKey(Context context, SharedPreferences sharedPreferences, String key) {

        String notifyOnNewCards = context.getString(R.string.PREFERENCES_notify_on_new_cards);

        if (notifyOnNewCards.equals(key)) {
            boolean enabled = sharedPreferences.getBoolean(key, true);
            processNewCardsSubscription(context, enabled);
        }

    }

    // Внутренние методы
    private static void processNewCardsSubscription(Context context, boolean isEnabled) {
        throw new RuntimeException(MyUtils.getString(context, R.string.not_implemented_yet));
    }
}
