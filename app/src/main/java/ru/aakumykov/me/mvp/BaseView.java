package ru.aakumykov.me.mvp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iAuthStateListener;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.login.Login_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthService;
import ru.aakumykov.me.mvp.services.AuthStateListener;
import ru.aakumykov.me.mvp.services.CardsService;
import ru.aakumykov.me.mvp.utils.MyUtils;


public abstract class BaseView extends AppCompatActivity implements
    iBaseView
{
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private final static String TAG = "BaseView";

    private Intent cardsServiceIntent;
    private Intent authServiceIntent;

    private ServiceConnection cardsServiceConnection;
    private ServiceConnection authServiceConnection;

    private iCardsService cardsService;
    private iAuthService authService;

    private boolean isCardsServiceBounded = false;
    private boolean isAuthServiceBounded = false;


    // Абстрактные методы
    public abstract void onServiceBounded();
    public abstract void onServiceUnbounded();

    public abstract void onUserLogin();
    public abstract void onUserLogout();


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Соединение со службой карточек
        cardsServiceIntent = new Intent(this, CardsService.class);

        cardsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG, "onCardsServiceConnected()");

                CardsService.LocalBinder localBinder = (CardsService.LocalBinder) service;
                cardsService = localBinder.getService();
                isCardsServiceBounded = true;

                onServiceBounded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG, "onCardsServiceDisconnected()");
                onServiceUnbounded();
                isCardsServiceBounded = false;
            }
        };

        // Соединение со службой авторизации
        authServiceIntent = new Intent(this, AuthService.class);

        authServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG, "onAuthServiceConnected()");

                AuthService.LocalBinder localBinder = (AuthService.LocalBinder) service;
                authService = localBinder.getService();
                isAuthServiceBounded = true;

                onServiceBounded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG, "onAuthServiceDisconnected()");
                onServiceUnbounded();
                isAuthServiceBounded = false;
            }
        };

        // Слушатель изменений авторизации
        iAuthStateListener authStateListener = new AuthStateListener(new iAuthStateListener.StateChangeCallbacks() {
            @Override
            public void onLoggedIn() {
                invalidateOptionsMenu();
                onUserLogin();
            }

            @Override
            public void onLoggedOut() {
                invalidateOptionsMenu();
                onUserLogout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(cardsServiceIntent, cardsServiceConnection, BIND_AUTO_CREATE);
        bindService(authServiceIntent, authServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isCardsServiceBounded)
            unbindService(cardsServiceConnection);

        if (isAuthServiceBounded)
            unbindService(authServiceConnection);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        menu.clear();

        if (isUserLoggedIn()) {
            Log.d(TAG, "User is logged IN");
            menuInflater.inflate(R.menu.user_in, menu);
            menuInflater.inflate(R.menu.logout, menu);
        } else {
            Log.d(TAG, "User is logged OUT");
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

            case R.id.actionLogin:
                login();
                break;

            case R.id.actionLogout:
                logout();
                break;

            case R.id.actionCreateTextCard:
                createCard(Constants.TEXT_CARD);
                break;

            case R.id.actionCreateImageCard:
                createCard(Constants.IMAGE_CARD);
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
    public void showErrorMsg(int messageId) {
        String message = getResources().getString(messageId);
        showErrorMsg(message);
    }

    @Override
    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
        Log.e(TAG, message);
    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage) {
        showErrorMsg(messageId);
        Log.e(TAG, consoleMessage);
    }

    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        MyUtils.show(messageView);
    }

    @Override
    public void hideMsg() {
        MyUtils.hide(messageView);
    }

    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }


    // Геттеры
    public iCardsService getCardsService() {
        return cardsService;
    }

    public iAuthService getAuthService() {
        return authService;
    }


    // Разное
    @Override
    public boolean isUserLoggedIn() {
        return authService.isUserLoggedIn();
    }

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
    public void enableUpButton() {
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
        authService.logout(new iAuthService.LogoutCallbacks() {
            @Override
            public void onLogoutSuccess() {
                showInfoMsg("Вы вышли");
            }

            @Override
            public void onLogoutFail(String errorMsg) {
                showErrorMsg(errorMsg);
            }
        });
    }

    private void createCard(String cardType) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);

        try {
            Card cardDraft = new Card();
            cardDraft.setType(cardType);
            intent.putExtra(Constants.CARD, cardDraft);

        } catch (Exception e) {
            showErrorMsg(R.string.ERROR_creating_card, e.getMessage());
            e.printStackTrace();
        }

        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void onCardCreated(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                showInfoMsg(R.string.INFO_card_created);
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
