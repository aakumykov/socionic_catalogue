package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;

public class PreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
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

    // TODO: что со статусом авторизации?

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        PreferencesProcessor.processPreferenceKey(getActivity(), sharedPreferences, key);

        if (key.equals(getString(R.string.PREFERENCES_notify_on_new_cards))) {

            SwitchPreference switchPreference = (SwitchPreference) findPreference(key);

//            if (switchPreference.isChecked()) {
//                switchPreference.setSummary(getString(R.string.PREFERENCES_notify_on_new_cards_enabled_summary));
//                subscribeToNewCardsNotifications();
//            } else {
//                switchPreference.setSummary(getString(R.string.PREFERENCES_notify_on_new_cards_disabled_summary));
//                unsubscribeFromNewCardsNotifications();
//            }
        }
    }



    public void subscribeToNewCardsNotifications() {

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_NEW_CARDS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "Вы подписаны на новые карточки";

                        if (!task.isSuccessful()) {
                            msg = "Ошибка подписки на новые карточки";
                        }

                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void unsubscribeFromNewCardsNotifications() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.TOPIC_NEW_CARDS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "Вы отписаны от уведомлений о новых карточках";

                        if (!task.isSuccessful()) {
                            msg = "Ошибка отписки от уведомлений о новых карточках";
                        }

                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
