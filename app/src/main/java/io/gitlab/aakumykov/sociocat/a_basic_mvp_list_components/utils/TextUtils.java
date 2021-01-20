package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextUtils {

    public static String getText(@Nullable Context context, int stringId, Object... formatArgs) {
        if (null != context)
            return context.getResources().getString(stringId, formatArgs);
        else
            return null;
    }

    public static String getPluralString(@NonNull Context context, int pluralResourceId, int count) {
        return getPluralString(context, pluralResourceId, null, count);
    }

    public static String getPluralString(@NonNull Context context, int pluralResourceId, @Nullable Integer zeroCountStringResourceId, int count) {
        if (null != zeroCountStringResourceId && 0 == count)
            return context.getResources().getString(zeroCountStringResourceId);
        else
            return context.getResources().getQuantityString(pluralResourceId, count, count);
    }
}
