package ru.aakumykov.me.sociocat.base_view;

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
    void showProgressMessage(Integer messageId);
    void showProgressMessage(@Nullable Integer messageId, String insertedText);
    void hideProgressMessage();

    void showProgressBar();
    void hideProgressBar();

    <T> void showDebugMsg(T msg);

    void showInfoMsg(int messageId, String... formatArguments);

    void showErrorMsg(@Nullable Integer messageId, @Nullable String consoleMessage);
    void showErrorMsg(@Nullable Integer messageId, @Nullable String consoleMessage, boolean forceShowConsoleMessage);

    void hideMessage();

    void showToast(@Nullable Integer msgId);
    void showToast(String message);

    void showLongToast(int msgId);
    void showLongToast(String message);

    // Заголовок страницы
    void setPageTitle(int titleId);
    void setPageTitle(int titleId, String insertedText);

    // Настройка страницы
    void activateUpButton();

    // Хранимые настройки
    SharedPreferences getSharedPrefs(String prefsName);
    void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName);

    void requestLogin(@Nullable Intent transitIntent);

    void closePage();
    void closePage(int resultCode, String action);

    String getString(int stringResourceId);

    String getString(int stringResourceId, String substitutedMessage);

    void startSomeActivity(Intent intent);

    Long getLastLoginTime();
    void updateLastLoginTime();

    void refreshMenu();
    void hideMenuItem(Menu menu, int menuItemId);

    void showUserProfile(String userId);

    void onPageRefreshed();

    void goToMainPage();
}
