package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
        addPreferencesFromResource(R.xml.settings);
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

    }
}
