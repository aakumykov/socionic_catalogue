package ru.aakumykov.me.sociocat;

public final class DeepLink_Constants {

    public static final String URL_BASE = "https://sociocat.example.org";

    // Пути URL
    public static final String PASSWORD_RESET_PATH = "/reset_password";
    public static final String REGISTRATION_STEP2_PATH = "/registration_step_2";
    public static final String CONFIRM_EMAIL_PATH = "/confirmEmail";

    // Ключи URL
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_ACTION = "action";
    public static final String KEY_EMAIL = "email";

    // Действия
    public static final String ACTION_CHANGE_EMAIL = "changeEmail";


    private DeepLink_Constants() {
        throw new RuntimeException("Creating object of class DeepLink_Constants is prohibited");
    }
}
