package ru.aakumykov.me.mvp;

import android.view.View;

import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public interface iBaseView {

    iCardsService getCardsService();
    iAuthService getAuthService();

    void onLoggedIn_TEMP();
    void onLoggedOut_TEMP();

    void showInfoMsg(int messageId);
    void showInfoMsg(String message);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);

    void showProgressBar();
    void hideProgressBar();

    void hideMsg();

    void closePage();
    void setPageTitle(int titleId);
    void setPageTitle(String title);
    void enableUpButton();

    View getProgressBar();
}
