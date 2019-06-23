package ru.aakumykov.me.sociocat.cards_grid.view_stubs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrig;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsGrid_ViewStub implements iCardsGrig.iPageView {

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
    public void hideProgressMessage() {

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
    public <T> void setTitle(T title) {

    }

    @Override
    public void goShowCard(Card card) {

    }

    @Override
    public void goCreateCard(Constants.CardType cardType) {

    }

    @Override
    public void goEditCard(Card card, int position) {

    }
}
