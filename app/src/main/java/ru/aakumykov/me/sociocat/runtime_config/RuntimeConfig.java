package ru.aakumykov.me.sociocat.runtime_config;

import android.os.Bundle;
import android.util.Log;

public class RuntimeConfig {

    /* Шаблон "одиночка" */
    private static volatile RuntimeConfig outInstance;
    public static synchronized RuntimeConfig getInstance() {
        synchronized (RuntimeConfig.class) {
            if (null == outInstance)
                outInstance = new RuntimeConfig();
            return outInstance;
        }
    }
    private RuntimeConfig() {
        settings = new Bundle();
    }
    /* Шаблон "одиночка" */


    // Свойства
    private static String TAG = "RuntimeConfig";
    private Bundle settings;


    // Внешние методы
    public static <T> void setValue(String key, T value) {
        getInstance().setSomeValue(key,value);
    }

    public static boolean getBool(String key) {
        return getInstance().getBooleanValue(key);
    }

    public static int getInt(String key, int defaultValue) {
        return getInstance().getIntegerValue(key, defaultValue);
    }

    public static String getString(String key) {
        return getInstance().getStringValue(key);
    }


    // Внутренние методы
    private <T> void setSomeValue(String key, T value) {
        if (value instanceof Boolean) {
            settings.putBoolean(key, (Boolean)value);
        }
        else if (value instanceof Integer) {
            settings.putInt(key, (Integer)value);
        }
        else if (value instanceof String) {
            settings.putString(key, (String)value);
        }
        else {
            Log.w(TAG, "Unsupported data type: "+value);
        }
    }

    private boolean getBooleanValue(String key) {
        if (hasValue(key)) return settings.getBoolean(key);
        else return false;
    }

    private int getIntegerValue(String key, int defaultValue) {
        if (hasValue(key)) return settings.getInt(key);
        else return defaultValue;
    }

    private String getStringValue(String key) {
        if (hasValue(key)) return settings.getString(key);
        else return null;
    }

    private boolean hasValue(String key) {
        return settings.containsKey(key);
    }
}
