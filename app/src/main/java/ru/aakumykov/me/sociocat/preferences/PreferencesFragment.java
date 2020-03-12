package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

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
            case "notify_about_new_cards":
                processNewCardsNotification();
                break;

            case "notify_on_comments":
                processCommentsNotification();
                break;

            default:
                Log.e(TAG, "Unknown preferences key '"+key+"'");
                break;
        }
    }

    private void processNewCardsNotification() {

    }

    private void processCommentsNotification() {

    }
}
