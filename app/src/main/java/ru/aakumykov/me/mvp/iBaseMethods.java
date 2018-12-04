package ru.aakumykov.me.mvp;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;

public interface iBaseMethods {

    // Аутентификация
    iAuthSingleton auth();

    // Графический интерфейс
    void showInfoMsg(int messageId);
    void showInfoMsg(String message);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);

    void showToast(int stringResourceId);
    void showToast(String msg);
    void showLongToast(String msg);

    void showProgressBar();
    void hideProgressBar();

    void hideMsg();
}
