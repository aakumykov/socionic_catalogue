package ru.aakumykov.me.sociocat;

public final class Constants {

    private Constants() {}

    public enum CardType {
        TEXT_CARD,
        IMAGE_CARD,
        AUDIO_CARD,
        VIDEO_CARD
    }

    public final static String  PACKAGE_NAME = "ru.aakumykov.me.sociocat";

    public final static String CARDS_PATH = "cards";
    public final static String TAGS_PATH = "tags";
    public final static String COMMENTS_PATH = "comments";
    public final static String IMAGES_PATH = "images";
    public final static String USERS_PATH = "users";
    public final static String ADMINS_PATH = "admins";
    public final static String AVATARS_PATH = "avatars";
    public final static String DEVICE_ID_PATH = "device_id";

    public final static String ACTION_CREATE = "ACTION_CREATE";
    public final static String ACTION_EDIT = "ACTION_EDIT";
    public final static String ACTION_EDIT_RESUME = "ACTION_EDIT_RESUME";
    public final static String ACTION_DELETE = "ACTION_DELETE";
    public final static String ACTION_LOGIN = "ACTION_LOGIN";
    public final static String ACTION_LOGIN_REQUEST = "ACTION_LOGIN_REQUEST";
    public final static String ACTION_LOGIN_FOR_COMMENT = "ACTION_LOGIN_FOR_COMMENT";
    public final static String ACTION_REGISTRATION_CONFIRM_REQUEST = "ACTION_REGISTRATION_CONFIRM_REQUEST";
    public final static String ACTION_REGISTRATION_CONFIRM_RESPONSE = "ACTION_REGISTRATION_CONFIRM_RESPONSE";
    public final static String ACTION_TRY_NEW_PASSWORD = "ACTION_TRY_NEW_PASSWORD";
    public final static String ACTION_REPLY_TO_CARD = "ACTION_REPLY_TO_CARD";
    public final static String ACTION_REPLY_TO_COMMENT = "ACTION_REPLY_TO_COMMENT";
    public static final String ACTION_SHOW_CARDS_WITH_TAG = "ACTION_SHOW_CARDS_WITH_TAG";

    public final static String CARD = "CARD";
    public final static String CARD_DRAFT = "CARD_DRAFT";
    public final static String CARD_KEY = "CARD_KEY";
    public final static String COMMENT_KEY = "COMMENT_KEY";

    public static final String TRANSIT_INTENT = "TRANSIT_INTENT";
    public final static String REPLIED_ITEM = "REPLIED_ITEM";

    public final static String TAG_FILTER = "TAG_FILTER";
    public final static String TAG_NAME = "TAG_NAME";

    public final static String USER = "USER";
    public final static String USER_ID = "USER_ID";
    public final static String USER_EMAIL = "USER_EMAIL";

    public final static String FILTER_KEY = "FILTER_KEY";

    public final static String TEXT_CARD = "TEXT_CARD";
    public final static String IMAGE_CARD = "IMAGE_CARD";
    public final static String AUDIO_CARD = "AUDIO_CARD";
    public final static String VIDEO_CARD = "VIDEO_CARD";

    public final static String MIME_TYPE_TEXT = "MIME_TYPE_TEXT";
    public final static String MIME_TYPE_IMAGE_DATA = "MIME_TYPE_IMAGE_DATA";
    public final static String MIME_TYPE_IMAGE_LINK = "MIME_TYPE_IMAGE_LINK";
    public final static String MIME_TYPE_YOUTUBE_VIDEO = "MIME_TYPE_YOUTUBE_VIDEO";
    public final static String MIME_TYPE_AUDIO = "MIME_TYPE_AUDIO";
    public final static String MIME_TYPE_UNKNOWN = "MIME_TYPE_UNKNOWN";

    public final static String PARENT_COMMENT = "PARENT_COMMENT";

    public final static String SOCIOCAT_NOTIFICATIONS_CHANNEL = "SOCIOCAT_NOTIFICATIONS_CHANNEL";

    public static final String BACK_BUTTON_ENABLED = "BACK_BUTON_ENABLED";

    // Разные коды
    public final static int CODE_LOGIN = 5;
    public final static int CODE_LOGIN_REQUEST = 6;
    public final static int CODE_CREATE_CARD = 15;
    public final static int CODE_EDIT_CARD = 20;
    public final static int CODE_SELECT_IMAGE = 25;
    public final static int CODE_USER_SHOW = 26;
    public final static int CODE_USER_EDIT = 30;
    public final static int CODE_FORCE_SETUP_USER_NAME = 40;
    public final static int CODE_POST_REPLY = 49;
    public final static int CODE_RESET_PASSWORD = 60;
    public final static int CODE_SHOW_CARD = 70;

    public final static String TAG_NUMBER_BRACE = "*";

    public final static String MODE_SEND = "MODE_SEND";
    public final static String MODE_SELECT = "MODE_SELECT";

    public final static int CARDS_GRID_QUOTE_MAX_LENGTH = 50;

    public final static int USER_NAME_MIN_LENGTH = 2;
    public final static int USER_NAME_MAX_LENGTH = 30;
    public final static int PASSWORD_MIN_LENGTH = 6;

    public final static String SHARED_PREFERENCES_EMAIL = "SHARED_PREFERENCES_EMAIL";
    public final static String SHARED_PREFERENCES_LOGIN = "SHARED_PREFERENCES_LOGIN";
    public final static String SHARED_PREFERENCES_CARD_EDIT = "SHARED_PREFERENCES_CARD_EDIT";

    public final static String KEY_LAST_LOGIN = "KEY_LAST_LOGIN";

    public final static String TOPIC_NEW_CARDS = "new_cards";

    public final static String PREFERENCE_KEY_IS_FIRST_RUN = "PREFERENCE_KEY_IS_FIRST_RUN";

    public static String DRAFT_DEFERRED = "DRAFT_DEFERRED";

    // Эти значения должны совпадать с атрибутами "action" в файлах shortcuts.xml
    public final static String SHORTCUT_CREATE_TEXT_CARD = "SHORTCUT_CREATE_TEXT_CARD";
    public final static String SHORTCUT_CREATE_IMAGE_CARD = "SHORTCUT_CREATE_IMAGE_CARD";
    public final static String SHORTCUT_CREATE_AUDIO_CARD = "SHORTCUT_CREATE_AUDIO_CARD";
    public final static String SHORTCUT_CREATE_VIDEO_CARD = "SHORTCUT_CREATE_VIDEO_CARD";

    // TODO: перенести в модель User
    public final static String USER_NAME_KEY = "name";
    public final static String USER_EMAIL_KEY = "email";
    public final static String USER_EMAIL_VERIFIED_KEY = "emailVerified";

    // TODO: перенести в модель Comment
    public final static String COMMENT_KEY_CARD_ID = "cardId";
    public final static String COMMENT_KEY_CREATED_AT = "createdAt";
}
