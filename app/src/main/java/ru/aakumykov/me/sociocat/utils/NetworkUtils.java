package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetworkUtils {

    public static boolean isAirplaneModeOn(Context context) {
        return 0 != Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0);

    }

    public static boolean isOffline(Context context) {

        if (isAirplaneModeOn(context))
            return true;

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetworkInfo)
            return true;

        return !activeNetworkInfo.isConnected();
    }

}
