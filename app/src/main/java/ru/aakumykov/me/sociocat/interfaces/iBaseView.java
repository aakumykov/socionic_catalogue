package ru.aakumykov.me.sociocat.interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

public interface iBaseView {

    // Контекст
    Context getAppContext();

    // Сообщения вверху страницы
    void showProgressMessage(int messageId);
    void hideProgressMessage();

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
    void requestLogin(int requestCode, @Nullable Bundle arguments);
    void requestLogin2(Bundle bundle);

    // Разное (УБРАТЬ!)
    void proceedLoginRequest(Intent intent);
    void goCreateCard();
    void closePage();

    String getString(int stringResourceId);
    void startSomeActivity(Intent intent);
}
