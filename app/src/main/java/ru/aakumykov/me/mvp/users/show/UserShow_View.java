package ru.aakumykov.me.mvp.users.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        iUsersSingleton.ReadCallbacks
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.aboutView) TextView aboutView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;

    private final static String TAG = "UserShow_View";
    private iUsers.Presenter presenter;

    // Где должен базироваться currentUser: во вьюхе или презентере?
    private User currentUser;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_show_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_page_title);

        showProgressBar();

        presenter = new Users_Presenter();

        try {
            Intent intent = getIntent();
            String userId = intent.getStringExtra(Constants.USER_ID);
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
            case RESULT_OK:
                break;
            case RESULT_CANCELED:
                return;
            default:
                showErrorMsg(R.string.USER_EDIT_error_saving_user, "Unknown resultCode: "+resultCode);
                return;
        }

        switch (requestCode) {
            case Constants.CODE_USER_EDIT:
                displayEditedUser(data);
                break;
//            case Constants.CODE_USER_DELETE:
//                break;
            default:
                break;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit, menu);
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
                goUserEdit();
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
    public void displayUser(final User user) {
        Log.d(TAG, "displayUser(), "+user);

        currentUser = user;

        hideProgressBar();

        nameView.setText(user.getName());
        aboutView.setText(user.getAbout());

//        MyUtils.show(nameLabel);
        MyUtils.show(nameView);
//        MyUtils.show(aboutLabel);
        MyUtils.show(aboutView);

        showAvatarThrobber();

        Picasso.get()
                .load(user.getAvatarURL())
                .into(avatarView, new Callback() {
                    @Override
                    public void onSuccess() {
                        hideAvatarThrobber();
                    }

                    @Override
                    public void onError(Exception e) {
                        hideAvatarThrobber();
                        showErrorMsg(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void goUserEdit() {
        Intent intent = new Intent(this, UserEdit_View.class);
        intent.putExtra(Constants.USER_ID, currentUser.getKey());
        startActivityForResult(intent, Constants.CODE_USER_EDIT);
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

//    private void processActivityResult(int requestCode, @Nullable Intent data) {
//        Log.d(TAG, "processActivityResult()");
//
////        switch (requestCode) {
////            case Constants.CODE_USER_EDIT:
////                break;
////            default:
////                showErrorMsg(R.string.internal_error, "Unknowm request code: "+requestCode);
////                break;
////        }
//
//        if (null != data) {
//
//            User user = data.getParcelableExtra(Constants.USER);
//
//            if (null != user) {
//                displayUser(user);
//            } else {
//                showErrorMsg(R.string.USER_EDIT_user_saving_error, "User from activity result data == null");
//            }
//
//        } else {
//            showErrorMsg(R.string.USER_EDIT_user_saving_error, "Activity result data == null");
//        }
//    }

    private void showAvatarThrobber() {
        MyUtils.show(avatarThrobber);
    }

    private void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber);
    }

    private void displayEditedUser(Intent data) {
        if (null == data) {
            showErrorMsg(R.string.USER_EDIT_data_error, "Intent data is null");
            return;
        }

        User user = data.getParcelableExtra(Constants.USER);
        if (null == user) {
            showErrorMsg(R.string.USER_EDIT_data_error, "User from intent is null.");
        }

        displayUser(user);
    }
}
