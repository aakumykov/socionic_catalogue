package ru.aakumykov.me.mvp.users.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.edit.UserEdit_View;
import ru.aakumykov.me.mvp.users.iUsers;

public class UserShow_View extends BaseView implements
        iUsers.ShowView,
        iUsersSingleton.UserCallbacks
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameLabel) TextView nameLabel;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.aboutLabel) TextView aboutLabel;
    @BindView(R.id.aboutView) TextView aboutView;

    private final static String TAG = "UserShow_View";
    private iUsers.Presenter presenter;

    private User currentUser;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_show_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_page_title);

        presenter = new Users_Presenter();

        try {
            Intent intent = getIntent();
            String userId = intent.getStringExtra(Constants.USER_ID);
            Log.d(TAG, "userId: "+userId);
            presenter.loadUser(userId, this);

        } catch (Exception e) {
            // TODO: всунуть сокрытие крутилки внутрь show*Message()
            hideProgressBar();
            showErrorMsg(R.string.error_displaying_user, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this);

        switch (resultCode) {
            case RESULT_CANCELED:
                return;
            case RESULT_OK:
                processActivityResult(requestCode, data);
                break;
            default:
                showErrorMsg(R.string.user_saving_error, "Unknown resultCode: "+resultCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onServiceBounded() {
        Log.d(TAG, "onServiceBounded()");
    }
    @Override
    public void onServiceUnbounded() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_delete, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");

        switch (item.getItemId()) {
            case android.R.id.home:
                closePage();
                break;
            case R.id.actionEdit:
                presenter.userEditClicked();
                break;
            case R.id.actionDelete:
                presenter.userDeleteClicked(currentUser.getKey());
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        closePage();
    }


    // Интерфейсные методы
    @Override
    public void displayUser(User user) {
        Log.d(TAG, "displayUser(), "+user);

        hideProgressBar();

        nameView.setText(user.getName());
        aboutView.setText(user.getAbout());

        MyUtils.show(nameLabel);
        MyUtils.show(nameView);
        MyUtils.show(aboutLabel);
        MyUtils.show(aboutView);
    }

    @Override
    public void goUserEdit() {
        Intent intent = new Intent(this, UserEdit_View.class);
        intent.putExtra(Constants.USER_ID, currentUser.getKey());
        startActivityForResult(intent, Constants.CODE_EDIT_USER);
    }


    // Коллбеки
    @Override
    public void onUserReadSuccess(User user) {
        currentUser = user;
        displayUser(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        currentUser = null;
        hideProgressBar();
        showErrorMsg(R.string.error_displaying_user, errorMsg);
    }


    // Внутренние методы
    private void processActivityResult(int requestCode, @Nullable Intent data) {
        Log.d(TAG, "processActivityResult()");

//        switch (requestCode) {
//            case Constants.CODE_EDIT_USER:
//                break;
//            default:
//                showErrorMsg(R.string.internal_error, "Unknowm request code: "+requestCode);
//                break;
//        }

        if (null != data) {

            User user = data.getParcelableExtra(Constants.USER);

            if (null != user) {
                displayUser(user);
            } else {
                showErrorMsg(R.string.user_saving_error, "User from activity result data == null");
            }

        } else {
            showErrorMsg(R.string.user_saving_error, "Activity result data == null");
        }
    }
}
