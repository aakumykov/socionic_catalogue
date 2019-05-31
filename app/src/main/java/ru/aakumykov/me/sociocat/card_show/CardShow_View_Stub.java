package ru.aakumykov.me.sociocat.card_show;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public class CardShow_View_Stub implements iCardShow_View {

    @Override
    public void showCommentForm(@Nullable String quotedText, ListItem parentItem) {

    }

    @Override
    public void hideCommentForm() {

    }

    @Override public void scrollListToPosition(int position) {

    }

    @Override public Context getAppContext() {
        return null;
    }

    @Override public void showProgressMessage(int messageId) {

    }

    @Override public void hideProgressMessage() {

    }

    @Override public void showErrorMsg(int userMessageId, String consoleMessage) {

    }

    @Override public void hideMsg() {

    }

    @Override public void showToast(int msgId) {

    }

    @Override public void showToast(String message) {

    }

    @Override public void setPageTitle(int titleId) {

    }

    @Override public void setPageTitle(int titleId, String insertedText) {

    }

    @Override public void activateUpButton() {

    }

    @Override public SharedPreferences getSharedPrefs(String prefsName) {
        return null;
    }

    @Override public void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName) {

    }

    @Override
    public void requestLogin(int requestCode, @Nullable Bundle arguments) {

    }

    @Override public void proceedLoginRequest(Intent intent) {

    }

    @Override public void goCreateCard() {

    }

    @Override public void closePage() {

    }

    @Override public String getString(int stringResourceId) {
        return null;
    }


    @Override public void startSomeActivity(Intent intent) {

    }
}
