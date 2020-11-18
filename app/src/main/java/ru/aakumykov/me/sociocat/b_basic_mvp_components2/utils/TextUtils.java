package ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils;


import android.content.Context;

import androidx.annotation.Nullable;

public class TextUtils {

    public static String getText(@Nullable Context context, int stringId, Object... formatArgs) {
        if (null != context)
            return context.getResources().getString(stringId, formatArgs);
        else
            return null;
    }
}
