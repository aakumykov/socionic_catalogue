package ru.aakumykov.me.sociocat.utils;

import androidx.annotation.Nullable;

public class YoutubeUtils {

    public static boolean isYoutubeVideoCode(@Nullable String text) {
        if (null == text)
            return false;

        return text.matches(CODE_PATTERN);
    }

    public static boolean isYoutubeLink(@Nullable String text) {

        if (null == text)
            return false;

        for (int i=0; i<LINK_PATTERNS.length; i++) {
            String regex = LINK_PATTERNS[i];
            if (text.matches(regex)) return true;
        }

        return false;
    }



    // !! Код YouTube-видео должен оказываться в 1й группе результата регулярного выражения !!
    private static final String CODE_PATTERN = "^(" + videoCodePattern() + ")$";

    private static final String[] LINK_PATTERNS = {
            "^https?://youtube\\.com/watch\\?v=(" + videoCodePattern() + ")",
            "^https?://www\\.youtube\\.com/watch\\?v=(" + videoCodePattern() + ")",
            "^https?://youtu.be/([^/]+)$"
    };

    private static String videoCodePattern() {
        return "[\\w-]+";
    }
}
