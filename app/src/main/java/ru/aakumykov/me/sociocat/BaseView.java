package ru.aakumykov.me.sociocat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.interfaces.iLoginRequester;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.preferences.PreferencesActivity;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.tags.list.TagsList_View;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public abstract class BaseView extends AppCompatActivity implements
        iBaseView,
        iLoginRequester
{
    public static String PACKAGE_NAME;
    private final static String TAG = "BaseView";

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();

    // EventBus
    @Subscribe
    public void onUserAuthorized(UserAuthorizedEvent event) {

        invalidateOptionsMenu();

        onUserLogin();

        saveLastLoginTime();
    }
    @Subscribe
    public void onUserUnauthorized(UserUnauthorizedEvent event) {

        invalidateOptionsMenu();

        onUserLogout();
    }


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        // TODO: попробовать перенести в MyApp
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkUnfinishedEdit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        if (AuthSingleton.isLoggedIn()) {
            menuInflater.inflate(R.menu.preferences, menu);
            menuInflater.inflate(R.menu.profile, menu);
            menuInflater.inflate(R.menu.logout, menu);
        } else {
            menuInflater.inflate(R.menu.login, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;

            case R.id.actionPreferences:
                openPreferences();
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


    // Контекст
    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }


    // Сообщения вверху страницы
    @Override
    public void showProgressMessage(int messageId) {
        Resources resources = getResources();
        showMsg(
                resources.getString(messageId),
                resources.getColor(R.color.info),
                resources.getColor(R.color.info_background)
        );
        showProgressBar();
    }

    @Override
    public void hideProgressMessage() {
        hideProgressBar();
        hideMsg();
    }

    @Override
    public void showErrorMsg(int messageId, @Nullable String consoleMessage) {
        String msg = (Config.DEBUG_MODE) ? consoleMessage : getResources().getString(messageId);
        hideProgressMessage();
        showMsg(msg, R.color.error, R.color.error_background);
        Log.e(TAG, consoleMessage);
    }

    @Override
    public void hideMsg() {
        TextView messageView = findViewById(R.id.messageView);
        TextView stackTraceView = findViewById(R.id.stackTraceView);

        if (null != messageView) {
            MyUtils.hide(messageView);
        }

        if (null != stackTraceView) {
            MyUtils.hide(stackTraceView);
        }
    }

    @Override
    public void showToast(int msgId) {
        MyUtils.showCustomToast(getAppContext(), msgId);
    }

    @Override
    public void showToast(String message) {
        MyUtils.showCustomToast(getAppContext(), message);
    }


    // Разное
    @Override
    public void requestLogin(int requestCode, @Nullable Bundle arguments) {

        Intent targetIntent = new Intent(this, Login_View.class);
               targetIntent.setAction(Constants.ACTION_LOGIN_REQUEST);
               targetIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

               //targetIntent.putExtra(Intent.EXTRA_INTENT, arguments);
               targetIntent.putExtra(Constants.EXTRA_ARGUMENTS, arguments);

       Intent argumentsIntent = new Intent();
       argumentsIntent.putExtra(Constants.COMMENT_KEY, "the-comment-key");
       targetIntent.putExtra(Intent.EXTRA_INTENT, argumentsIntent);

        startActivityForResult(targetIntent, requestCode);
    }

    @Override public void requestLogin2(int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, Login_View.class);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void startSomeActivity(Intent intent) {
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
        String title = getResources().getString(titleId);
        setPageTitle(title);
    }

    @Override
    public void setPageTitle(int titleId, String insertedText) {
        String title = getResources().getString(titleId, insertedText);
        setPageTitle(title);
    }

    @Override
    public void activateUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) actionBar.setDisplayHomeAsUpEnabled(true);
    }


    // Внутренние методы
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

    private void showProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar)
            MyUtils.show(progressBar);
    }

    private void hideProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar)
            MyUtils.hide(progressBar);
    }

    private void setPageTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle(title);
        }
    }

    private void login() {
        // Можно и без result, потому что статус авторизации обрабатывается в
        // AuthStateListener
        if (!AuthSingleton.isLoggedIn()) {
            //Log.d(TAG, "doLogin()");
            Intent intent = new Intent(this, Login_View.class);
            startActivityForResult(intent, Constants.CODE_LOGIN);
        }
    }

    private void logout() {
        AuthSingleton.logout();
    }

    private void openPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void seeUserProfile() {
        MyUtils.showCustomToast(getAppContext(), R.string.not_implemented_yet);
        /*try {
            Intent intent = new Intent(this, UserShow_View.class);
            intent.putExtra(Constants.USER_ID, AuthSingleton.currentUserId());
            startActivity(intent);
        } catch (Exception e) {
            showErrorMsg(e.getMessage());
            e.printStackTrace();
        }*/
    }

    private void onCardEdited(int resultCode, @Nullable Intent data) {
        MyUtils.showCustomToast(getAppContext(), R.string.not_implemented_yet);
//        switch (resultCode) {
//            case RESULT_OK:
//                showToast(R.string.INFO_card_saved);
//                break;
//            case RESULT_CANCELED:
//                showToast(R.string.INFO_operation_cancelled);
//                break;
//            default:
//                showErrorMsg(R.string.ERROR_saving_card);
//                Log.e(TAG, "data: "+data);
//                break;
//        }
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
