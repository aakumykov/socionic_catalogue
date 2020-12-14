package ru.aakumykov.me.sociocat;

public final class AppConfig {

    private AppConfig() {}

    // Длины
    public final static int TAG_MIN_LENGTH = 2;
    public final static int TAG_MAX_LENGTH = 40;
    public final static int TITLE_MIN_LENGTH = 2;
    public final static int TITLE_MAX_LENGTH = 70;
    public final static int QUOTE_MIN_LENGTH = 5;
    public final static int QUOTE_MAX_LENGTH = 10000;
    public final static int DESCRIPTION_MIN_LENGTH = 2;
    public final static int DESCRIPTION_MAX_LENGTH = 2000;
    public final static int DIALOG_MESSAGE_LENGTH = 40;
    public final static int LONG_TAG_THRESHOLD = 500;
    public final static int PASSWORD_MIN_LENGTH = 6;

    public static final int AVATAR_MAX_WIDTH = 256;
    public static final int AVATAR_MAX_HEIGHT = 455;
    public static final int AVATAR_MAX_SIZE = 256;

    public static final int DEFAULT_JPEG_QUALITY = 90;

    public static final int MAX_CARD_IMAGE_WIDTH = 1920;
    public static final int MAX_CARD_IMAGE_HEIGHT = 1080;

    /* Важно!!: код видео должен быть забран в скобки, чтобы выделяться в
    регулярном выражении, как группа с индексом 1. */
    public static final String YOUTUBE_CODE_REGEX = "^([\\w-]+)$";
    public static final String YOUTUBE_SHORT_LINK_REGEX = "^https?://youtu.be/([^/]+)$";
    public static final String YOUTUBE_LONG_LINK_REGEX_1 = "^https?://youtube\\.com/watch\\?v=([\\w-]+)";
    public static final String YOUTUBE_LONG_LINK_REGEX_2 = "^https?://www\\.youtube\\.com/watch\\?v=([\\w-]+)";

    public static final int DEFAULT_CARDS_LOAD_COUNT    = 15;
    public static final int DEFAULT_COMMENTS_LOAD_COUNT = 10;

    public static final int CARDS_GRID_COLUMNS_COUNT_PORTRAIT = 2;
    public static final int CARDS_GRID_COLUMNS_COUNT_LANDSCAPE = 4;
    public static final int CARDS_GRID_QUOTE_LENGTH = 50;

    public static final String CREATE_CUSTOM_TOKEN_BASE_URL = "https://us-central1-sociocat-debug.cloudfunctions.net";
    public static final String CREATE_CUSTOM_TOKEN_PATH = "/vkAccessToken2FirebaseAccessToken";
    public static final String CREATE_CUSTOM_TOKEN_PARAMETER_NAME = "token";

    public static final String SAVED_CARDS_LIST = "SAVED_CARDS_LIST";
    public static final String CARDS_LIST = "CARDS_LIST";

    public static final int CARDS_RATING_COUNTERS_NUMBER = 10;

    public static final int CREATE_TEMP_USER_NAME_TRIES_COUNT = 10;

    public static final long BACKUP_START_DELAY_IN_SECONDS = 10;
    public static final long BACKUP_INTERVAL_IN_SECONDS = 3600; // каждый час

    public static final long NEW_CARDS_CHECK_DELAY = 1000;
    public static final long NEW_CARDS_CHECK_INTERVAL = 20000;
}
