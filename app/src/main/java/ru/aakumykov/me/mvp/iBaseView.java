package ru.aakumykov.me.mvp;

public interface iBaseView {

    void showInfoMsg(int messageId);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);

    void showProgressBar();
    void hideProgressBar();

    void hideMsg();

    void closePage();
}
