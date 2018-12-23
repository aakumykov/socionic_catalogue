package ru.aakumykov.me.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.aakumykov.me.mvp.card.edit.CardEdit_View;
import ru.aakumykov.me.mvp.card_edit2.CardEdit2_View;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iAuthStateListener;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.login.Login_View;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.AuthStateListener;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.users.show.UserShow_View;
import ru.aakumykov.me.mvp.utils.MyUtils;

public abstract class BaseView extends AppCompatActivity implements iBaseView
{
    private final static String TAG = "BaseView";
    private iCardsSingleton cardsService;
    private iAuthSingleton authService;

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // TODO: убрать вообще?
        authService = AuthSingleton.getInstance();
        cardsService = CardsSingleton.getInstance();
        // TODO: storageSingleton

        // Слушатель изменений авторизации
        iAuthStateListener authStateListener = new AuthStateListener(new iAuthStateListener.StateChangeCallbacks() {

            // Осторожно с этими методами!
            @Override
            public void onLoggedIn() {
                invalidateOptionsMenu();
//                showToast("onLoggedIn()");
                onUserLogin();
            }

            // Осторожно с этими методами!
            @Override
            public void onLoggedOut() {
                invalidateOptionsMenu();
//                showToast("onLoggedOut()");
                onUserLogout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        if (auth().isUserLoggedIn()) {
            menuInflater.inflate(R.menu.user_in, menu);
            menuInflater.inflate(R.menu.logout, menu);
        } else {
            menuInflater.inflate(R.menu.user_out, menu);
            menuInflater.inflate(R.menu.login, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;

            case R.id.actionUserProfile:
                seeUserProfile();
                break;

            case R.id.actionLogin:
                login();
                break;

            case R.id.actionLogout:
                logout();
                break;

            case R.id.actionCreate:
                createCard();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult()");

        switch (requestCode) {

            case Constants.CODE_LOGIN:
                break;

            case Constants.CODE_CREATE_CARD:
                onCardCreated(resultCode, data);
                break;

            case Constants.CODE_EDIT_CARD:
                onCardEdited(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
        TextView messageView = findViewById(R.id.messageView);
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
        TextView messageView = findViewById(R.id.messageView);
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
        showToastReal(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showLongToast(String msg) {
        showToastReal(this, msg, Toast.LENGTH_LONG);
    }

    private void showToastReal(Context context, String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    // Строка прогресса
    @Override
    public void showProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (null != progressBar) {
            MyUtils.show(progressBar);
        } else {
            Log.w(TAG, "progressBar not found");
        }
    }

    @Override
    public void hideProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (null != progressBar) {
            MyUtils.hide(progressBar);
        } else {
            Log.w(TAG, "progressBar not found");
        }
    }


    // Геттеры
    public iCardsSingleton getCardsService() {
        return cardsService;
    }

    public iAuthSingleton auth() {
        return authService;
    }


    // Разное
    @Override
    public void closePage() {
        Log.d(TAG, "closePage()");
        finish();
    }

    @Override
    public void setPageTitle(int titleId) {
        Log.d(TAG, "setPageTitle("+titleId+")");
        String title = getResources().getString(titleId);
        setPageTitle(title);
    }

    @Override
    public void setPageTitle(String title) {
        Log.d(TAG, "setPageTitle("+title+")");
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void activateUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) actionBar.setDisplayHomeAsUpEnabled(true);
    }


    // Внутренние методы
    private void login() {
        // Можно и без result, потому что статус авторизации обрабатывается в
        // AuthStateListener
        if (!authService.isUserLoggedIn()) {
            Log.d(TAG, "doLogin()");
            Intent intent = new Intent(this, Login_View.class);
            startActivityForResult(intent, Constants.CODE_LOGIN);
        }
    }

    private void logout() {
        Log.d(TAG, "logout()");
        authService.logout();
    }

    private void seeUserProfile() {
        try {
            Intent intent = new Intent(this, UserShow_View.class);
            intent.putExtra(Constants.USER_ID, authService.currentUserId());
            startActivity(intent);
        } catch (Exception e) {
            showErrorMsg(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createCard() {
        Intent intent = new Intent(this, CardEdit_View.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setAction(Constants.ACTION_CREATE);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void onCardCreated(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                showToast(R.string.INFO_card_created);
                break;
            case RESULT_CANCELED:
                showInfoMsg(R.string.INFO_operation_cancelled);
                break;
            default:
                showErrorMsg(R.string.ERROR_creating_card);
                Log.d(TAG, "data: "+data);
                break;
        }
    }

    private void onCardEdited(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                showInfoMsg(R.string.INFO_card_saved);
                break;
            case RESULT_CANCELED:
                showInfoMsg(R.string.INFO_operation_cancelled);
                break;
            default:
                showErrorMsg(R.string.ERROR_saving_card);
                Log.d(TAG, "data: "+data);
                break;
        }
    }
}
