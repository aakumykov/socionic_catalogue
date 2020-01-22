package ru.aakumykov.me.sociocat;

public final class DynamicLink_Constants {

    public static final String ACTION_URL_BASE = "https://sociocat.example.org";

    public static final String PASSWORD_RESET_PATH = "/reset_password?uid=";

    public static final String REGISTRATION_STEP2_PATH = "/registration_step_2";


    private DynamicLink_Constants() {
        throw new RuntimeException("Creating object of class DynamicLink_Constants is prohibited");
    }
}
