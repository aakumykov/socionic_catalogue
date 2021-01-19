package io.gitlab.aakumykov.sociocat.utils;

import android.util.Log;

import io.gitlab.aakumykov.sociocat.BuildConfig;

public class LogUtils {


    public static void d(String tag, Object s) {
        if (BuildConfig.DEBUG)
            Log.d(tag, s.toString());
    }
}
