package ru.aakumykov.me.sociocat.z_base_view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public interface iBaseView {

    // Контекст
    Context getAppContext();
    Activity getActivity();

    // Состояние страницы
    void setViewState(iBasicViewState viewState);

    // Сообщения вверху страницы
    void showProgressMessage(int messageId);
    void showProgressMessage(String msg);

    void hideProgressMessage();

    void showProgressBar();
    void hideProgressBar();

    <T> void showDebugMsg(T msg);

    void showInfoMsg(int messageId, String... formatArguments);

    void showErrorMsg(int messageId, @Nullable String consoleMessage);
    void showErrorMsg(int messageId, @Nullable String consoleMessage, boolean forceShowConsoleMessage);

    void hideMessage();

    void showToast(int msgId);
    void showToast(String message);

    void showSnackbar(int msgId, int dismissStringResourceId);
    void showSnackbar(int msgId, int dismissStringResourceId, @Nullable Integer duration);
    void showSnackbar(String text, int dismissStringResourceId);
    void showSnackbar(String text, int dismissStringResourceId, @Nullable Integer duration);

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

    String getString(int stringResourceId, int substitutedMessage);

    String getString(int stringResourceId, String substitutedMessage);

    void startSomeActivity(Intent intent);

    Long getLastLoginTime();
    void updateLastLoginTime();

    void refreshMenu();
    void hideMenuItem(Menu menu, int menuItemId);
    void clearMenu();
    void inflateMenu(int menuId);
    void makeMenuItemVisible(int menuItemId, @Nullable Integer menuIconId);

    void showUserProfile(String userId);

    void onPageRefreshed();

    void goToMainPage();
}
