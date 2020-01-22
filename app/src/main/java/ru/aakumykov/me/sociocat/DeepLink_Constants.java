package ru.aakumykov.me.sociocat;

public final class DeepLink_Constants {

    public static final String URL_BASE = "https://sociocat.example.org";

    public static final String PASSWORD_RESET_PATH = "/reset_password?uid=";

    public static final String REGISTRATION_STEP2_PATH = "/registration_step_2";

    public static final String CONFIRM_EMAIL_PATH = "/confirmEmail";


    private DeepLink_Constants() {
        throw new RuntimeException("Creating object of class DeepLink_Constants is prohibited");
    }
}
