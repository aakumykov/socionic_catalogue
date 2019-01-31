package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private SharedPreferences sharedPreferences;

    public MySharedPreferences(Context context, String prefsName) {
        new MySharedPreferences(context, prefsName, Context.MODE_PRIVATE);
    }

    public MySharedPreferences(Context context, String prefsName, int mode) {
        this.sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);

    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public void store(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }
}
