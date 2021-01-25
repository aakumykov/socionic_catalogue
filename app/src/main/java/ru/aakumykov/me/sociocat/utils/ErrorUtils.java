package ru.aakumykov.me.sociocat.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ErrorUtils {

    public static String getErrorFromException(@Nullable Exception e, @NonNull String fallbackErrorMsg) {

        if (null != e) {
            String errorMsg = e.getMessage();
            if (null != errorMsg)
                return errorMsg;
        }

        return fallbackErrorMsg;
    }
}
