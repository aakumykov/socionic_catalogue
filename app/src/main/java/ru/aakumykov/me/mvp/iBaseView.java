package ru.aakumykov.me.mvp;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;

public interface iBaseView {

    iCardsSingleton getCardsService();
    iAuthSingleton getAuthService();

    boolean userLoggedIn();

    void showInfoMsg(int messageId);
    void showInfoMsg(String message);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);

    void showProgressBar();
    void hideProgressBar();

    void hideMsg();

    void setPageTitle(int titleId);
    void setPageTitle(String title);
    void activateUpButton();

    void closePage();
}
