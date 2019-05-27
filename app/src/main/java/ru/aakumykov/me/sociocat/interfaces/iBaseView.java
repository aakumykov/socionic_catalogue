package ru.aakumykov.me.sociocat.interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.Exclude;

public interface iBaseView {

    // Контекст
    Context getAppContext();

    // Сообщения вверху страницы
    void showProgressMessage(int messageId);
    void hideProgressMessage();

    void showInfoMsg(int messageId);
    void showInfoMsg(String message);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);
    void showErrorMsg(int userMessageId, Exception e);

    <T> void showConsoleError(String tag, T arg);

    // Всплывающие сообщения
    void showToast(int stringResourceId);
    void showToast(String msg);
    void showLongToast(String msg);
    void showLongToast(int msgId);
    void showToast(int stringResourceId, int gravity);
    void showLongToast(int stringResourceId, int gravity);

    void hideMsg();

    // Индикатор активности
    void showProgressBar();
    void hideProgressBar();

    void consoleMsg(String tag, String msg);

    // Заголовок страницы
    void setPageTitle(int titleId);
    void setPageTitle(int titleId, String insertedText);

    // Настройка страницы
    void activateUpButton();

    // Хранимые настройки
    SharedPreferences getSharedPrefs(String prefsName);
    void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName);


    // Разное (УБРАТЬ!)
    void proceedLoginRequest(Intent intent);
    void goCreateCard();
    void closePage();

    String getString(int stringResourceId);
    void requestLogin(Intent originalIntent);
    void startSomeActivity(Intent intent);
}
