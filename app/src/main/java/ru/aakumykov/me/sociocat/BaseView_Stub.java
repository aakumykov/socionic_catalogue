package ru.aakumykov.me.sociocat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public class BaseView_Stub implements iBaseView {

    @Override
    public Context getAppContext() {
        return null;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void showProgressMessage(int messageId) {

    }

    @Override
    public void showProgressMessage(int messageId, String insertedText) {

    }

    @Override
    public void hideProgressMessage() {

    }

    @Override
    public <T> void showDebugMsg(T msg) {

    }

    @Override
    public void showErrorMsg(int userMessageId, String consoleMessage) {

    }

    @Override
    public void hideMsg() {

    }

    @Override
    public void showToast(int msgId) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void setPageTitle(int titleId) {

    }

    @Override
    public void setPageTitle(int titleId, String insertedText) {

    }

    @Override
    public void activateUpButton() {

    }

    @Override
    public SharedPreferences getSharedPrefs(String prefsName) {
        return null;
    }

    @Override
    public void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName) {

    }

    @Override
    public <T> void requestLogin(int requestCode, @Nullable T transitArguments) {

    }

    @Override
    public void proceedLoginRequest(Intent intent) {

    }

    @Override
    public void goCreateCard() {

    }

    @Override
    public void closePage() {

    }

    @Override
    public String getString(int stringResourceId) {
        return null;
    }

    @Override
    public String getString(int stringResourceId, String substitutedMessage) {
        return null;
    }

    @Override
    public void startSomeActivity(Intent intent) {

    }

    @Override
    public Long getLastLoginTime() {
        return null;
    }
}
