package ru.aakumykov.me.sociocat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_type_chooser.CardTypeChooser;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.event_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.tags.list.TagsList_View;
import ru.aakumykov.me.sociocat.users.show.UserShow_View;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public abstract class BaseView extends AppCompatActivity implements iBaseView
{
    public static String PACKAGE_NAME;
    private final static String TAG = "BaseView";
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        // TODO: попробовать перенести в MyApp
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Subscribe
    public void onUserAuthorized(UserAuthorizedEvent event) {
        showToast("Авторизовался "+event.getUser().getKey());

        authService.storeCurrentUser(event.getUser());

        Log.d(TAG, "Авторизовался, 01");
        invalidateOptionsMenu();
        supportInvalidateOptionsMenu();
        Log.d(TAG, "Авторизовался, 02");

        onUserLogin();

        saveLastLoginTime();
    }

    @Subscribe
    public void onUserUnauthorized(UserUnauthorizedEvent event) {
        showToast("Разавторизовался");

        authService.clearCurrentUser();

        Log.d(TAG, "Разавторизовался, 01");
        invalidateOptionsMenu();
        Log.d(TAG, "Разавторизовался, 02");

        onUserLogout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUnfinishedEdit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu()");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");

        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        if (auth().isUserLoggedIn()) {
            menuInflater.inflate(R.menu.profile, menu);
            menuInflater.inflate(R.menu.logout, menu);
        } else {
//            menuInflater.inflate(R.menu.user_out, menu);
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

            case R.id.actionProfile:
                seeUserProfile();
                break;

            case R.id.actionLogin:
                login();
                break;

            case R.id.actionLogout:
                logout();
                break;

            case R.id.actionCreate:
                goCreateCard();
                break;

            case R.id.actionCards:
//                goCardsList();
                goCardsGrid();
                break;

            case R.id.actionTags:
                goTagsList();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.d(TAG, "onActivityResult()");

        switch (requestCode) {

            case Constants.CODE_LOGIN:
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
    public void showProgressMessage(int messageId) {
        showInfoMsg(messageId);
        showProgressBar();
    }

    @Override
    public void hideProgressMessage() {
        hideProgressBar();
        hideMsg();
    }

    @Override
    public void showInfoMsg(int messageId) {
        Resources resources = getResources();
        showMsg(resources.getString(messageId), resources.getColor(R.color.info), resources.getColor(R.color.info_background));
    }

    @Override
    public void showInfoMsg(String message) {
        Resources resources = getResources();
        showMsg(message, resources.getColor(R.color.info), resources.getColor(R.color.info_background));
    }

    @Override
    public void showInfoMsg(int userMessageId, String consoleMessage) {
        showInfoMsg(userMessageId);
        Log.d(TAG, consoleMessage);
    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage) {
        String msg = (Config.DEBUG_MODE) ? consoleMessage : getResources().getString(messageId);
        showErrorMsg(msg);
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
        showMsg(message, getResources().getColor(R.color.error), getResources().getColor(R.color.error_background));
        Log.e(TAG, message);
    }

    @Override
    public <T> void showConsoleError(String tag, T arg) {
        String msg = "";
        if (arg instanceof Integer) {
            msg = getResources().getString((Integer) arg);
        }
        else {
            msg = (String)arg;
        }
        Log.e(tag, msg);
    }

    private void showMsg(String text, int color) {
        showMsg(text, color, null);
    }

    private void showMsg(String text, int textColor, @Nullable Integer backgroundColor) {
        TextView messageView = findViewById(R.id.messageView);

        if (null == backgroundColor)
            backgroundColor = getResources().getColor(R.color.background_default);

        if (null != messageView) {
            messageView.setText(text);
            messageView.setTextColor(textColor);
            messageView.setBackgroundColor(backgroundColor);
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

    @Override
    public void consoleMsg(String tag, String msg) {
        Log.d(tag, msg);
    }


    // Всплывающие сообщения
    @Override
    public void showToast(int stringResourceId) {
        String msg = getString(stringResourceId);
        showToast(msg);
    }

    @Override public void showToast(int stringResourceId, int gravity) {
        String msg = getString(stringResourceId);
        showToastReal(this, msg, Toast.LENGTH_SHORT, gravity);
    }

    @Override public void showLongToast(int stringResourceId, int gravity) {
        String msg = getString(stringResourceId);
        showToastReal(this, msg, Toast.LENGTH_LONG, gravity);
    }

    @Override
    public void showToast(String msg) {
        showToastReal(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showLongToast(int msgId) {
        String msg = getResources().getString(msgId);
        showLongToast(msg);
    }

    @Override
    public void showLongToast(String msg) {
        showToastReal(this, msg, Toast.LENGTH_LONG);
    }

    private void showToastReal(Context context, String message, int length) {
        showToastReal(context, message, length, Gravity.NO_GRAVITY);
    }

    private void showToastReal(Context context, String message, int length, int gravity) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }


    // Строка прогресса
    @Override
    public void showProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar) {
            MyUtils.show(progressBar);
        } else {
            Log.w(TAG, "progressBar not found");
        }
    }

    @Override
    public void hideProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
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
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void startMyActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public SharedPreferences getSharedPrefs(String prefsName) {
        return getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    @Override
    public void clearSharedPrefs(SharedPreferences sharedPreferences, String dataName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(dataName);
        editor.apply();
    }

    @Override
    public void goCreateCard() {
        Intent intent = new Intent(this, CardTypeChooser.class);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    @Override
    public void closePage() {
        //Log.d(TAG, "closePage()");
        finish();
    }

    @Override
    public void setPageTitle(int titleId) {
        //Log.d(TAG, "setPageTitle("+titleId+")");
        String title = getResources().getString(titleId);
        setPageTitle(title);
    }

    @Override
    public void setPageTitle(String title) {
        //Log.d(TAG, "setPageTitle("+title+")");
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
            //Log.d(TAG, "doLogin()");
            Intent intent = new Intent(this, Login_View.class);
            startActivityForResult(intent, Constants.CODE_LOGIN);
        }
    }

    private void logout() {
        //Log.d(TAG, "logout()");
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

    private void onCardEdited(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                showToast(R.string.INFO_card_saved);
                break;
            case RESULT_CANCELED:
                showToast(R.string.INFO_operation_cancelled);
                break;
            default:
                showErrorMsg(R.string.ERROR_saving_card);
                Log.e(TAG, "data: "+data);
                break;
        }
    }

    private void goCardsList() {
        Intent intent = new Intent(this, CardsList_View.class);
        startActivity(intent);
    }

    private void goCardsGrid() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    private void goTagsList() {
        Intent intent = new Intent(this, TagsList_View.class);
        startActivity(intent);
    }

    private void checkUnfinishedEdit() {
        final SharedPreferences sharedPreferences = getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);

        String className = getClass().getSimpleName();

        if (!getClass().getSimpleName().equals("CardEdit_View")) {

            if (sharedPreferences.contains(Constants.CARD)) {

                MyDialogs.resumeCardEditDialog(this, new iMyDialogs.StandardCallbacks() {
                    @Override
                    public void onCancelInDialog() {
                        checkUnfinishedEdit();
                    }

                    @Override
                    public void onNoInDialog() {
                        clearSharedPrefs(sharedPreferences, Constants.CARD);
                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        Intent intent = new Intent(BaseView.this, CardEdit_View.class);
                        intent.setAction(Constants.ACTION_EDIT_RESUME);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void saveLastLoginTime() {
        SharedPreferences sharedPreferences = getSharedPrefs(Constants.SHARED_PREFERENCES_LOGIN);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.KEY_LAST_LOGIN, new Date().getTime());
        editor.apply();
    }

}
