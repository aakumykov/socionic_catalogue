package ru.aakumykov.me.mvp;

public final class Constants {
    private Constants() {}

    public final static String CARDS_PATH = "/cards";
    public final static String TAGS_PATH = "/tags";
    public final static String COMMENTS_PATH = "/comments";
    public final static String IMAGES_PATH = "/images";
    public final static String USERS_PATH = "/users";

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

    public final static int CODE_LOGIN = 5;
    public final static int CODE_CREATE_CARD = 15;
    public final static int CODE_EDIT_CARD = 20;
    public final static int CODE_SELECT_IMAGE = 25;
    public final static int CODE_EDIT_USER = 30;
    public final static int TAG_MIN_LENGTH = 2;
    public final static int TAG_MAX_LENGTH = 20;
    public final static int TITLE_MAX_LENGTH = 70;

    public final static String MODE_SEND = "MODE_SEND";
    public final static String MODE_SELECT = "MODE_SELECT";

}
