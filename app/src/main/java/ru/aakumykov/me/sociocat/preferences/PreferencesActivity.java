package ru.aakumykov.me.sociocat.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class PreferencesActivity extends BaseView {

    private PreferencesFragment mPreferencesFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(R.string.PREFERENCES_ACTIVITY_page_title);
        activateUpButton();

        mPreferencesFragment = new PreferencesFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, mPreferencesFragment)
                .commit();
    }

    @Override
    public void onUserLogin() {
        mPreferencesFragment.assemblePreferences();
    }

    @Override
    public void onUserLogout() {
        mPreferencesFragment.assemblePreferences();
    }
}
