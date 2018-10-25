package ru.aakumykov.me.mvp.users.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.edit.UserEdit_View;
import ru.aakumykov.me.mvp.users.iUsers;

public class UserShow_View extends BaseView implements
        iUsers.ShowView,
        iUsersSingleton.UserCallbacks
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameRow) LinearLayout nameRow;
    @BindView(R.id.emailRow) LinearLayout emailRow;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;

    private final static String TAG = "UserShow_View";
    private iUsers.Presenter presenter;

    private User currentUser;

    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_activity);
        ButterKnife.bind(this);

        presenter = new Users_Presenter();

        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constants.USER_ID);
        Log.d(TAG, "userId: "+userId);

        try {
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

        if (RESULT_CANCELED == resultCode) return;

        // TODO: как обрабатывать ошибки?
        if (RESULT_OK == resultCode) {
            showInfoMsg(R.string.user_saved);

            Intent intent = getIntent();
            Log.d(TAG, intent.toString());

            User user = intent.getParcelableExtra(Constants.USER);
            Log.d(TAG, "user: "+user);

//            displayUser(user);
        }
        else {
            showErrorMsg(R.string.error_saving_user);
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

    }
    @Override
    public void onServiceUnbounded() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_show_menu, menu);
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


    // Пользовательские методы
    @Override
    public void displayUser(User user) {
        Log.d(TAG, "displayUser(), "+user);

        hideProgressBar();

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());

        MyUtils.show(nameRow);
        MyUtils.show(emailRow);
    }

    @Override
    public void goUserEdit() {
        Intent intent = new Intent(this, UserEdit_View.class);
        intent.putExtra(Constants.USER_ID, currentUser.getKey());
        startActivityForResult(intent, Constants.CODE_EDIT_USER);
    }


    //TODO: перенести в общий интерфейс
    void hideProgressBar() {
        MyUtils.hide(progressBar);
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
        showErrorMsg(R.string.error_displaying_user, errorMsg);
    }

}
