package ru.aakumykov.me.sociocat.a_basic_mvp_components.utils;

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
