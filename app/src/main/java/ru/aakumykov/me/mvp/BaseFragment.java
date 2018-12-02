package ru.aakumykov.me.mvp;

import android.support.v4.app.Fragment;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;

public abstract class BaseFragment extends Fragment implements iBaseView {

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();

    @Override
    public iAuthSingleton getAuthService() {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }

    @Override
    public void showInfoMsg(int messageId) {

    }

    @Override
    public void showInfoMsg(String message) {

    }

    @Override
    public void showInfoMsg(int messageId, String consoleMessage) {

    }

    @Override
    public void showErrorMsg(int messageId) {

    }

    @Override
    public void showErrorMsg(String message) {

    }

    @Override
    public void showErrorMsg(int userMessageId, String consoleMessage) {

    }

    @Override
    public void showToast(int stringResourceId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showLongToast(String msg) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void hideMsg() {

    }

    @Override
    public void setPageTitle(int titleId) {

    }

    @Override
    public void setPageTitle(String title) {

    }

    @Override
    public void activateUpButton() {

    }

    @Override
    public void closePage() {

    }
}
