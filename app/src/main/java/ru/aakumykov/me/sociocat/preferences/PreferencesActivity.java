package ru.aakumykov.me.sociocat.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;

public class PreferencesActivity extends BaseView {

    // FragmentActivity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPageTitle(R.string.PREFERENCES_ACTIVITY_page_title);
        activateUpButton();

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareDefaultPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        finish();
    }


    // Внутренние
    private void prepareDefaultPreferences() {

        // Подготавливаю необходимые компоненты
//        Context appContext = getApplicationContext();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Проверяю, первый ли это запуск программы?
        boolean isFirstRun = defaultSharedPreferences.getBoolean(Constants.PREFERENCE_KEY_is_first_run, true);

        // Если это первый запуск, устанавдиваю в механизме настроек значения по умолчанию и обрабатываю их все
        if (isFirstRun) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            //PreferencesProcessor.processAllPreferences(this, defaultSharedPreferences);

            // Помечаю, что теперь это не первый запуск
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCE_KEY_is_first_run, false);
            editor.apply();
        }
    }
}

