package io.gitlab.aakumykov.sociocat;

public final class FirebaseConstants {

    public static final String ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD";
    public static final String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
    public static final String ERROR_INVALID_ACTION_CODE = "ERROR_INVALID_ACTION_CODE";

    // Вымышленная константа (в Firebase такой нет) для внутреннего пользования
    public static final String TOO_MANY_LOGIN_ATTEMPTS = "TOO_MANY_LOGIN_ATTEMPTS";

    private FirebaseConstants() {}
}
