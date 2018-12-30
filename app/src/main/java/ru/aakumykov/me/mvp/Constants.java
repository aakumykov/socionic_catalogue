package ru.aakumykov.me.mvp;

public final class Constants {
    private Constants() {}

    public final static String CARDS_PATH = "/cards";
    public final static String TAGS_PATH = "/tags";
    public final static String COMMENTS_PATH = "/comments";
    public final static String IMAGES_PATH = "/images";
    public final static String USERS_PATH = "/users";
    public final static String AVATARS_PATH = "/avatars";

    public final static String CARD = "CARD";
    public final static String CARD_KEY = "CARD_KEY";

    public final static String ACTION_CREATE = "ACTION_CREATE";
    public final static String ACTION_EDIT = "ACTION_EDIT";

    public final static String TAG_FILTER = "TAG_FILTER";
    public final static String TAG_KEY = "TAG_KEY";

    public final static String USER = "USER";
    public final static String USER_ID = "USER_ID";

    public final static String TEXT_CARD = "TEXT_CARD";
    public final static String IMAGE_CARD = "IMAGE_CARD";
    public final static String AUDIO_CARD = "AUDIO_CARD";
    public final static String VIDEO_CARD = "VIDEO_CARD";

    public final static String TYPE_TEXT = "TYPE_TEXT";
    public final static String TYPE_IMAGE_DATA = "TYPE_IMAGE_DATA";
    public final static String TYPE_IMAGE_LINK = "TYPE_IMAGE_LINK";
    public final static String TYPE_YOUTUBE_VIDEO = "TYPE_YOUTUBE_VIDEO";
    public final static String TYPE_AUDIO = "TYPE_AUDIO";
    public final static String TYPE_UNKNOWN = "TYPE_UNKNOWN";

    public final static String PARENT_COMMENT = "PARENT_COMMENT";

    public final static int CODE_LOGIN = 5;
    public final static int CODE_CREATE_CARD = 15;
    public final static int CODE_EDIT_CARD = 20;
    public final static int CODE_SELECT_IMAGE = 25;
    public final static int CODE_USER_EDIT = 30;
    public final static int CODE_FORCE_SETUP_USER_NAME = 40;

    public final static int TAG_MIN_LENGTH = 2;
    public final static int TAG_MAX_LENGTH = 40;
    public final static int TITLE_MAX_LENGTH = 70;
    public final static int DIALOG_MESSAGE_LENGTH = 40;

    public final static String MODE_SEND = "MODE_SEND";
    public final static String MODE_SELECT = "MODE_SELECT";

}
