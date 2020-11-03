package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.provider.Settings;

public class NetworkUtils {

    public static boolean isAirplaneModeOn(Context context) {
        return 0 != Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0);

    }
}
