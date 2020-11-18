package ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LogUtils {

    public static <T> void printError(@NonNull String tag, @Nullable T e) {

        if (null != e)
        {
            if (e instanceof Exception) {
                Exception exception = ((Exception) e);

                String msg = exception.getMessage();

                if (null != msg)
                    Log.e(tag, msg);

                for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                    Log.e(tag, stackTraceElement.toString());
                }
            } else {
                Log.e(tag, String.valueOf(e));
            }
        }
    }

}
