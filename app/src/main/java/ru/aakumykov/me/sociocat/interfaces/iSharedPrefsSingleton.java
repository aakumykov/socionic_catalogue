package ru.aakumykov.me.sociocat.interfaces;

import android.content.SharedPreferences;

public interface iSharedPrefsSingleton {

    SharedPreferences getSharedPrefs(String name);

}
