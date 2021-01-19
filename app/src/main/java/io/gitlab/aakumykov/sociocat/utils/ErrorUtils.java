package io.gitlab.aakumykov.sociocat.utils;

import androidx.annotation.NonNull;

public class ErrorUtils {

    public static String getErrorFromException(@NonNull Exception e, @NonNull String fallbackErrorMsg) {
        String errorMsg = e.getMessage();
        if (null == errorMsg)
            errorMsg = fallbackErrorMsg;
        return errorMsg;
    }
}
