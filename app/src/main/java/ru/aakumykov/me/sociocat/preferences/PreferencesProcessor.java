package ru.aakumykov.me.sociocat.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

import androidx.annotation.NonNull;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;

public class PreferencesProcessor {

    public static void processAllPreferences(Context context, SharedPreferences sharedPreferences) {

        for (Map.Entry<String,?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            processPreferenceKey(context, sharedPreferences, key);
        }
    }

    public static void processPreferenceKey(Context context, SharedPreferences sharedPreferences, String key) {

        String notifyOnNewCards = context.getString(R.string.PREFERENCES_notify_on_new_cards);

        if (notifyOnNewCards.equals(key)) {
            boolean enabled = sharedPreferences.getBoolean(key, true);

            if (enabled) subscribeToNewCardsNotifications(context);
            else unsubscribeFromNewCardsNotifications(context);

            return;
        }

        if ("other option".equals(key)) {
            return;
        }
    }

    private static void processKey(Context context, Object referenceKey, String key, Object value) {
        if (referenceKey.equals(key)) {
            if ((Boolean)value) {
                subscribeToNewCardsNotifications(context);
            } else {
                unsubscribeFromNewCardsNotifications(context);
            }
        }
    }


    private static void subscribeToNewCardsNotifications(Context context) {

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_NEW_CARDS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "Вы подписаны на новые карточки";

                        if (!task.isSuccessful()) {
                            msg = "Ошибка подписки на новые карточки";
                        }

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private static void unsubscribeFromNewCardsNotifications(Context context) {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.TOPIC_NEW_CARDS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "Вы отписаны от уведомлений о новых карточках";

                        if (!task.isSuccessful()) {
                            msg = "Ошибка отписки от уведомлений о новых карточках";
                        }

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
