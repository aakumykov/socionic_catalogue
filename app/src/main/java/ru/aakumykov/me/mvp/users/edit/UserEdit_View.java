package ru.aakumykov.me.mvp.users.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.iUsers;


public class UserEdit_View extends BaseView implements
        iUsers.EditView,
        iUsersSingleton.UserCallbacks,
        iUsersSingleton.SaveCallbacks,
        View.OnClickListener
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.aboutInput) EditText aboutInput;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "UserEdit_View";
    private iUsers.Presenter presenter;
    private User currentUser;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        ButterKnife.bind(this);

        presenter = new Users_Presenter();

        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constants.USER_ID);

        try {
            presenter.prepareUserEdit(userId, this);
        } catch (Exception e) {

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

    @OnClick({R.id.saveButton, R.id.cancelButton})
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick()");

        switch (v.getId()) {

            case R.id.saveButton:
                presenter.saveButtonClicked(currentUser.getKey(), this);
                break;

            case R.id.cancelButton:
                presenter.cancelButtonClicked();
                break;

            default:
                Log.e(TAG, "Clicked button with unknown id: "+v.getId());
        }
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void fillUserForm(User user) {
        Log.d(TAG, "fillUserForm()");
        hideProgressBar();
        nameInput.setText(user.getName());
        aboutInput.setText(user.getAbout());
    }

    @Override
    public String getName() {
        return nameInput.getText().toString();
    }

    @Override
    public String getAbout() {
        return aboutInput.getText().toString();
    }


    // Коллбеки
    @Override
    public void onUserReadSuccess(User user) {
        currentUser = user;
        fillUserForm(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        currentUser = null;
        showErrorMsg(R.string.error_loading_data);
    }

    @Override
    public void onUserSaveSuccess(User user) {
        Log.d(TAG, "onUserSaveSuccess(), "+user);

        Intent intent = new Intent();
        intent.putExtra(Constants.USER, user);
        setResult(RESULT_OK, intent);

        closePage();
    }

    @Override
    public void onUserSaveFail(String errorMsg) {
        hideProgressBar();
        enableEditForm();
        showErrorMsg(R.string.user_saving_error, errorMsg);
    }


    // Методы интерфейса
    @Override
    public void enableEditForm() {
        nameInput.setEnabled(true);
        aboutInput.setEnabled(true);
        saveButton.setEnabled(true);
    }

    @Override
    public void disableEditForm() {
        nameInput.setEnabled(false);
        aboutInput.setEnabled(false);
        saveButton.setEnabled(false);
    }
}
