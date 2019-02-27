package ru.aakumykov.me.sociocat.interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.User;

public interface iBaseView {

    iAuthSingleton auth();

    Context getAppContext();
    void startMyActivity(Intent intent);

    SharedPreferences getSharedPrefs(String prefsName);
    void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName);

    void showProgressMessage(int messageId);
    void hideProgressMessage();

    void showInfoMsg(int messageId);
    void showInfoMsg(String message);
    void showInfoMsg(int messageId, String consoleMessage);

    void showErrorMsg(int messageId);
    void showErrorMsg(String message);
    void showErrorMsg(int userMessageId, String consoleMessage);

    <T> void showConsoleError(String tag, T arg);

    void showToast(int stringResourceId);
    void showToast(String msg);
    void showLongToast(String msg);
    void showLongToast(int msgId);
    void showToast(int stringResourceId, int gravity);
    void showLongToast(int stringResourceId, int gravity);

    void showProgressBar();
    void hideProgressBar();

    void hideMsg();

    void consoleMsg(String tag, String msg);

    void setPageTitle(int titleId);
    void setPageTitle(String title);
    void activateUpButton();

    void goCreateCard();
    void closePage();
}
