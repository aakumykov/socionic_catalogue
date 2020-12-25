package ru.aakumykov.me.sociocat.preferences2;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class Preferences2_Activity extends BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(R.string.PREFERENCES_ACTIVITY_page_title);
        activateUpButton();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Preferences2_Fragment())
                .commit();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
