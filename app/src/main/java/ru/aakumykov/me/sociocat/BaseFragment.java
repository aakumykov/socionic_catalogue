package ru.aakumykov.me.sociocat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iAuthStateListener;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.AuthStateListener;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public abstract class BaseFragment extends Fragment implements iBaseView
{
    private final static String TAG = "BaseFragment";
    private iCardsSingleton cardsService;
    private iAuthSingleton authService;

    private View rooView;

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // TODO: убрать вообще?
        authService = AuthSingleton.getInstance();
        cardsService = CardsSingleton.getInstance();

        Log.d(TAG, "authService: "+authService);
        Log.d(TAG, "cardsService: "+cardsService);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public iAuthSingleton auth() {
        return authService;
    }


    // Сообщения пользователю
    @Override
    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    @Override
    public void showInfoMsg(String message) {
        showMsg(message, getResources().getColor(R.color.info));
    }

    @Override
    public void showInfoMsg(int userMessageId, String consoleMessage) {
        showInfoMsg(userMessageId);
        Log.d(TAG, consoleMessage);
    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage) {
        showErrorMsg(messageId);
        Log.e(TAG, consoleMessage);
    }

    @Override
    public void showErrorMsg(int messageId) {
        hideProgressBar();
        String message = getResources().getString(messageId);
        showErrorMsg(message);
    }

    @Override
    public void showErrorMsg(String message) {
        hideProgressBar();
        showMsg(message, getResources().getColor(R.color.error));
        Log.e(TAG, message);
    }

    private void showMsg(String text, int color) {
        TextView messageView = getRooView().findViewById(R.id.messageView);
        if (null != messageView) {
            messageView.setText(text);
            messageView.setTextColor(color);
            MyUtils.show(messageView);
        } else {
            Log.w(TAG, "messageView not found");
        }
    }

    @Override
    public void hideMsg() {
        TextView messageView = getRooView().findViewById(R.id.messageView);
        if (null != messageView) {
            MyUtils.hide(messageView);
        } else {
            Log.w(TAG, "messageView not found");
        }
    }


    // Тосты
    @Override
    public void showToast(int stringResourceId) {
        String msg = getString(stringResourceId);
        showToast(msg);
    }

    @Override
    public void showToast(String msg) {
        showToastReal(getContext(), msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showLongToast(String msg) {
        showToastReal(getContext(), msg, Toast.LENGTH_LONG);
    }

    private void showToastReal(Context context, String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    // Строка прогресса
    @Override
    public void showProgressBar() {
        ProgressBar progressBar = getRooView().findViewById(R.id.progressBar);
        if (null != progressBar) {
            MyUtils.show(progressBar);
        } else {
            Log.w(TAG, "progressBar not found");
        }
    }
    @Override
    public void hideProgressBar() {
        ProgressBar progressBar = getRooView().findViewById(R.id.progressBar);
        if (null != progressBar) {
            MyUtils.hide(progressBar);
        } else {
            Log.w(TAG, "progressBar not found");
        }
    }


    // Служебные страничные
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


    // Вспомогательные
    public void setRootView(View view) {
        this.rooView = view;
    }

    public View getRooView() {
        return this.rooView;
    }
}
