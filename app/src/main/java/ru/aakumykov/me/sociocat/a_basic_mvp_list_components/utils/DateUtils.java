package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils;


import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private DateUtils() { }


    private static final String TAG = DateUtils.class.getSimpleName();


    public static String long2date(Long seconds) {
        return long2date(seconds, null);
    }

    public static String long2date(Long seconds, @Nullable String dateFormat) {
        if (null == dateFormat)
            dateFormat = "dd LLL YYYY, HH:mm:ss";

        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.US);
            df.setTimeZone(tz);
            return df.format(new Date(seconds * 1000));
        }
        catch (Exception e) {
            String message = e.getMessage();
            if (null != message)
                Log.e(TAG, message);
            e.printStackTrace();
            return "00:00:00";
        }
    }

}
