package ru.aakumykov.me.sociocat.utils;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IntentUtils {

    public static ContentType detectContentType(@Nullable Intent intent) {
        if (null == intent)
            return null;

        String type = intent.getType();

        if (null == type)
            return ContentType.NO_CONTENT_TYPE;

        type = type.trim();

        if (TextUtils.isEmpty(type))
            return ContentType.NO_CONTENT_TYPE;

        if (type.startsWith("image/")) {
            return ContentType.IMAGE;
        }
        else if ("text/plain".equals(type)) {
            return detectTextType(type);
        }
        else {
            return ContentType.OTHER;
        }
    }



    private static ContentType detectTextType(@NonNull String type) {

        if (YoutubeUtils.isYoutubeLink(type))
            return ContentType.YOUTUBE_VIDEO;
        else
            return ContentType.TEXT;
    }
}
