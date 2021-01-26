package ru.aakumykov.me.sociocat.utils;

import android.util.Log;

import ru.aakumykov.me.sociocat.BuildConfig;

public class LogUtils {


    public static void d(String tag, Object s) {
        if (BuildConfig.DEBUG)
            Log.d(tag, s.toString());
    }
}
