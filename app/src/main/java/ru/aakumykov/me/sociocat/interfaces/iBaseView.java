package ru.aakumykov.me.sociocat.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import androidx.annotation.Nullable;

public interface iBaseView {

    // Контекст
    Context getAppContext();
    Activity getActivity();

    // Сообщения вверху страницы
    void showProgressMessage(int messageId);
    void showProgressMessage(int messageId, String insertedText);
    void hideProgressMessage();

    void showProgressBar();
    void hideProgressBar();

    <T> void showDebugMsg(T msg);

    void showErrorMsg(int userMessageId, String consoleMessage);
    void hideMsg();

    void showToast(int msgId);
    void showToast(String message);

    // Заголовок страницы
    void setPageTitle(int titleId);
    void setPageTitle(int titleId, String insertedText);

    // Настройка страницы
    void activateUpButton();

    // Хранимые настройки
    SharedPreferences getSharedPrefs(String prefsName);
    void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName);

    // Разное
    <T> void requestLogin(int requestCode, @Nullable T transitArguments);

    // Разное (УБРАТЬ!)
    void proceedLoginRequest(Intent intent);
    void goCreateCard();
    void closePage();

    String getString(int stringResourceId);
    String getString(int stringResourceId, String substitutedMessage);

    void startSomeActivity(Intent intent);

    Long getLastLoginTime();

    void reloadMenu();
    void hideMenuItem(Menu menu, int menuItemId);
}
