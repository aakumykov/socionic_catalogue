package ru.aakumykov.me.sociocat;

public final class Config {

    private Config() {}

    public final static boolean DEBUG_MODE = true;

    public final static String YOUTUBE_API_KEY = "AIzaSyDmoNN5aMSvJflpt6bjQdTWfPc7CHtz3i4";

    public static final int AVATAR_MAX_WIDTH = 256;
    public static final int AVATAR_MAX_HEIGHT = 455;

    public static final String DEFAULT_IMAGE_TYPE = "jpg";
    public static final int DEFAULT_JPEG_QUALITY = 90;

    public static final int MAX_CARD_IMAGE_WIDTH = 1920;
    public static final int MAX_CARD_IMAGE_HEIGHT = 1080;

    /* Важно!!: код видео должен быть забран в скобки, чтобы выделяться в
    регулярном выражении, как группа с индексом 1. */
    public static final String YOUTUBE_CODE_REGEX = "^([\\w-]+)$";
    public static final String YOUTUBE_SHORT_LINK_REGEX = "^https?://youtu.be/([^/]+)$";
    public static final String YOUTUBE_LONG_LINK_REGEX_1 = "^https?://youtube\\.com/watch\\?v=([\\w-]+)";
    public static final String YOUTUBE_LONG_LINK_REGEX_2 = "^https?://www\\.youtube\\.com/watch\\?v=([\\w-]+)";

    public static final int DEFAULT_CARDS_LOAD_COUNT = 200;
    public static final int DEFAULT_COMMENTS_LOAD_COUNT = 10;

    public static final int CARDS_GRID_COLUMNS_COUNT_PORTRAIT = 2;
    public static final int CARDS_GRID_COLUMNS_COUNT_LANDSCAPE = 4;
}
