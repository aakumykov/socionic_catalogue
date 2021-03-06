package ru.aakumykov.me.sociocat.z_base_view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

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
    public void setViewState(iViewState viewState) {

    }

    @Override
    public void showProgressMessage(int messageId) {

    }

    @Override
    public void showProgressMessage(String msg) {

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
    public void showErrorMsg(int messageId, String consoleMessage) {

    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage, boolean forceShowConsoleMessage) {

    }

    @Override
    public void hideMessage() {

    }

    @Override
    public void showToast(int msgId) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showSnackbar(int msgId, int dismissStringResourceId) {

    }

    @Override
    public void showSnackbar(int msgId, int dismissStringResourceId, @Nullable Integer duration) {

    }

    @Override
    public void showSnackbar(String text, int dismissStringResourceId) {

    }

    @Override
    public void showSnackbar(String text, int dismissStringResourceId, @Nullable Integer duration) {

    }

    @Override
    public void showLongToast(int msgId) {

    }

    @Override
    public void showLongToast(String message) {

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
    public void requestLogin(@NonNull Intent transitIntent) {

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
    public String getString(int stringResourceId, int substitutedMessage) {
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
    public void clearMenu() {

    }

    @Override
    public void inflateMenu(int menuId) {

    }

    @Override
    public void makeMenuItemVisible(int menuItemId, @Nullable Integer menuIconId) {

    }

    @Override
    public void showUserProfile(String userId) {

    }

    @Override
    public void onPageRefreshed() {

    }

    @Override
    public void goToMainPage() {

    }
}
