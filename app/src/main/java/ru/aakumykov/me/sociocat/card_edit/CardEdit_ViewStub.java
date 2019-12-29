package ru.aakumykov.me.sociocat.card_edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Menu;

import androidx.annotation.Nullable;

import java.util.HashMap;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.Card;

public class CardEdit_ViewStub extends BaseView_Stub implements iCardEdit.View {

    @Override
    public void displayCard(Card card) {

    }

    @Override
    public void displayImage(String imageURI) {

    }

    @Override
    public void displayVideo(String videoCode, @Nullable Float timecode) {

    }

    @Override
    public void displayAudio(String audioCode, @Nullable Float timecode) {

    }

    @Override
    public void removeImage() {

    }

    @Override
    public void removeMedia() {

    }

    @Override
    public String getCardTitle() {
        return null;
    }

    @Override
    public String getQuote() {
        return null;
    }

    @Override
    public String getQuoteSource() {
        return null;
    }

    @Override
    public Bitmap getImageBitmap() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Float getTimecode() {
        return null;
    }

    @Override
    public HashMap<String, Boolean> getTags() {
        return null;
    }

    @Override
    public void convert2audio() {

    }

    @Override
    public void convert2video() {

    }

    @Override
    public void showTitleError(int msgId) {

    }

    @Override
    public void showQuoteError(int msgId) {

    }

    @Override
    public void showVideoError(int msgId) {

    }

    @Override
    public void showAudioError(int msgId) {

    }

    @Override
    public void showMediaError() {

    }

    @Override
    public void hideMediaError() {

    }

    @Override
    public void showDescriptionError(int msgId) {

    }

    @Override
    public void showImageError(int msgId) {

    }

    @Override
    public void hideImageError() {

    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }

    @Override
    public void showImageProgressBar() {

    }

    @Override
    public void hideImageProgressBar() {

    }

    @Override
    public boolean isFormFilled() {
        return false;
    }

    @Override
    public void finishEdit(Card card) {

    }

    @Override
    public void showCard(Card card) {

    }

    @Override
    public void addTag(String tag) {

    }

    @Override
    public void showDraftRestoreDialog(Card cardDraft) {

    }

    @Override
    public float pauseMedia() {
        return 0;
    }

    @Override
    public void resumeMedia(float position) {

    }

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
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public <T> void showDebugMsg(T msg) {

    }

    @Override
    public void showInfoMsg(int messageId, String... formatArguments) {

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
    public void closePage(int resultCode, String action) {

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

    @Override
    public void updateLastLoginTime() {

    }

    @Override
    public void refreshMenu() {

    }

    @Override
    public void hideMenuItem(Menu menu, int menuItemId) {

    }

    @Override
    public void showUserProfile(String userId) {

    }
}
