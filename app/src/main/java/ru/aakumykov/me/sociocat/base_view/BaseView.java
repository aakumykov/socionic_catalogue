package ru.aakumykov.me.sociocat.base_view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.BuildConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.backup_job.BackupService;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.preferences.PreferencesActivity;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.tags_lsit3.TagsList3_View;
import ru.aakumykov.me.sociocat.user_show.UserShow_View;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.auth.GoogleAuthHelper;

public abstract class BaseView extends AppCompatActivity implements iBaseView
{
    private final static String TAG = "BaseView";
    private boolean intentMessageAlreadyShown = false;

    // Абстрактные методы
    public abstract void onUserLogin();
    public abstract void onUserLogout();

    // EventBus
    @Subscribe
    public void onUserAuthorized(UserAuthorizedEvent event) {
        invalidateOptionsMenu();

        hideMessage();

        onUserLogin();
    }
    @Subscribe
    public void onUserUnauthorized(UserUnauthorizedEvent event) {
        invalidateOptionsMenu();

        onUserLogout();

        GoogleAuthHelper.logout(this);
    }


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveLastLoginTime();
        makeBackup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        processIntentMessage(getIntent());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("intentMessageAlreadyShown", intentMessageAlreadyShown);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.intentMessageAlreadyShown = savedInstanceState.getBoolean("intentMessageAlreadyShown", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        //checkUnfinishedEdit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

//        if (BuildConfig.DEBUG)
//            menuInflater.inflate(R.menu.probe, menu);

//        menuInflater.inflate(R.menu.tags, menu);

        if (AuthSingleton.isLoggedIn()) {
//            menuInflater.inflate(R.menu.preferences, menu);
//            menuInflater.inflate(R.menu.profile_in, menu);
            menuInflater.inflate(R.menu.logout, menu);
        }
        else {
//            menuInflater.inflate(R.menu.profile_out, menu);
            menuInflater.inflate(R.menu.login, menu);
        }

        return super.onCreateOptionsMenu(menu);
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
                onUserProfileClicked();
                break;

            case R.id.actionLogin:
                login();
                break;

            case R.id.actionLogout:
                logout();
                break;

            case R.id.actionCards:
                goCardsGrid();
                break;

            case R.id.actionTags:
                goTagsList();
                break;

            case R.id.actionProbe:
                //doProbe();
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

    @Override
    public Activity getActivity() {
        return this;
    }


    // Сообщения вверху страницы
    @Override
    public void showProgressMessage(int messageId) {
        String msg = getResources().getString(messageId);
        showProgressMessage(msg);
    }

    @Override
    public void showProgressMessage(int messageId, String insertedText) {
        String msg = getResources().getString(messageId, insertedText);
        showProgressMessage(msg);
    }

    @Override
    public void hideProgressMessage() {
        hideProgressBar();
        hideMessage();
    }

    @Override
    public void showProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar)
            MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar)
            MyUtils.hide(progressBar);
    }

    @Override
    public <T> void showDebugMsg(T msg) {
        if (BuildConfig.DEBUG) {
            hideProgressMessage();
            String text = "";
            if (msg instanceof Integer) {
                text = getResources().getString((Integer) msg);
                showMsg(text, R.color.debug, R.color.white);
            }
            else {
                text = String.valueOf(msg);
                showMsg(text, R.color.debug, R.color.white);
            }
            Log.d("showDebugMsg(): ", text);
        }
    }

    @Override
    public void showInfoMsg(int messageId, String... formatArguments) {
        String text = getResources().getString(messageId, formatArguments);
        hideProgressMessage();
        showMsg(text, R.color.info, R.color.info_background);
    }

    @Override
    public void showErrorMsg(int messageId, @Nullable String consoleMessage) {
        boolean showConsoleMessage = BuildConfig.DEBUG && null != consoleMessage;
        showErrorMsg(messageId, consoleMessage, showConsoleMessage);
    }

    @Override
    public void showErrorMsg(int messageId, @Nullable String consoleMessage, boolean forceShowConsoleMessage) {
        String msg = getResources().getString(messageId);

        if (forceShowConsoleMessage) {
            msg = msg + ": " + consoleMessage;
            Log.e(TAG, "" + consoleMessage);
        }

        hideProgressMessage();
        showMsg(msg, R.color.error, R.color.error_background);
    }

    @Override
    public void hideMessage() {
        intentMessageAlreadyShown = true;

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

    @Override
    public void showLongToast(int msgId) {
        MyUtils.showLongCustomToast(getAppContext(), msgId);
    }

    @Override
    public void showLongToast(String message) {
        MyUtils.showLongCustomToast(getAppContext(), message);
    }


    @Override
    public void requestLogin(@Nullable Intent transitIntent) {
        Intent loginIntent = new Intent(this, Login_View.class);
        loginIntent.setAction(Constants.ACTION_LOGIN);
        loginIntent.putExtra(Constants.TRANSIT_INTENT, transitIntent);
        startActivityForResult(loginIntent, Constants.CODE_LOGIN_REQUEST);
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
    public void closePage() {
        finish();
    }

    @Override
    public void closePage(int resultCode, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public String getString(int stringResourceId, String substitutedMessage) {
        return getResources().getString(stringResourceId, substitutedMessage);
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

    @Override
    public Long getLastLoginTime() {
        long currentTime = new Date().getTime();

        if (!AuthSingleton.isLoggedIn()) {
            return currentTime;
        }
        else {
            SharedPreferences sharedPreferences = getSharedPrefs(Constants.SHARED_PREFERENCES_LOGIN);
            return sharedPreferences.getLong(Constants.KEY_LAST_LOGIN, currentTime);
        }
    }

    @Override
    public void updateLastLoginTime() {
        saveLastLoginTime();
    }

    @Override
    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void hideMenuItem(Menu menu, int menuItemId) {
        MenuItem menuItem = menu.findItem(R.id.actionNewCards);
        if (null != menuItem)
            menuItem.setVisible(false);
    }

    @Override
    public void showUserProfile(String userId) {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void onPageRefreshed() {
        hideMessage();
    }

    @Override
    public void goToMainPage() {
        startActivity(new Intent(this, CardsGrid_View.class));
    }


    // Внутренние методы
    private void showProgressMessage(String msg) {
        Resources resources = getResources();
        showMsg(
                msg,
                R.color.info,
                R.color.info_background
        );
        showProgressBar();
    }

    private void showMsg(String text, int textColorId, @Nullable Integer backgroundColorId) {
        TextView messageView = findViewById(R.id.messageView);

        int fgColor = getResources().getColor(textColorId);
        int bgColor = getResources().getColor(null != backgroundColorId ? backgroundColorId : R.color.background_default);

        if (null != messageView) {
            messageView.setText(text);
            messageView.setTextColor(fgColor);
            messageView.setBackgroundColor(bgColor);
            MyUtils.show(messageView);
        }
        else {
            Log.w(TAG, "messageView or progressAndMessageContainer not found");
        }
    }

    private void setPageTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle(title);
        }
    }

    private void processIntentMessage(@Nullable Intent intent) {
        if (null != intent && !intentMessageAlreadyShown) {
            int infoMsgId = intent.getIntExtra(Constants.INFO_MESSAGE_ID, -1);

            int errorMsgId = intent.getIntExtra(Constants.ERROR_MESSAGE_ID, -1);
            String consoleError = intent.getStringExtra(Constants.CONSOLE_ERROR_MESSAGE);

            if (infoMsgId > -1)
                showLongToast(infoMsgId);

            if (errorMsgId > -1 && null != consoleError)
                showErrorMsg(errorMsgId, consoleError);
        }
    }

    private void login() {
        // Можно и без result, потому что статус авторизации обрабатывается в
        // AuthStateListener
        if (!AuthSingleton.isLoggedIn()) {
            //Log.d(TAG, "doLogin()");
            Intent intent = new Intent(this, Login_View.class);
            intent.setAction(Constants.ACTION_LOGIN);
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

    private void onUserProfileClicked() {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, AuthSingleton.currentUserId());
        startActivity(intent);
    }

    private void onCardEdited(int resultCode, @Nullable Intent data) {
//        MyUtils.showCustomToast(getAppContext(), R.string.not_implemented_yet);
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

    private void goCardsGrid() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    private void goTagsList() {
        Intent intent = new Intent(this, TagsList3_View.class);
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

    private void makeBackup() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                produceBackup();
            }
        };

        new Timer().schedule(task, AppConfig.BACKUP_DELAY_IN_SECONDS);
    }

    private void produceBackup() {
        boolean isTimeToDoBackup = BackupService.isTimeToDoBackup(this);

        if (isTimeToDoBackup) {

            boolean isAdmin = UsersSingleton.getInstance().currentUserIsAdmin();
            boolean backupIsEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(Constants.PREFERENCE_KEY_perform_database_backup, false);
            boolean backupIsRunning  = BackupService.isRunning();

            if (backupIsEnabled && isAdmin && !backupIsRunning) {
                try {
                    startService(new Intent(this, BackupService.class));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
}
