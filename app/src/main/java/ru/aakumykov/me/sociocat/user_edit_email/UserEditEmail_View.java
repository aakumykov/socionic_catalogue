package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserEditEmail_View extends BaseView implements iUserEditEmail.iView {

    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iUserEditEmail.iPresenter presenter;

    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_email_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_EDIT_EMAIL_page_title);
        activateUpButton();

        UserEmailEdit_ViewModel viewModel = new ViewModelProvider(this, new UserEmailEdit_ViewModelFactory())
                .get(UserEmailEdit_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserEditEmail_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (hasEmail())
            presenter.onConfigChanged();
        else
            presenter.onFirstOpen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                return presenter.onHomePressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        finish();
    }


    // iUserEditEmail.iView
    @Override
    public void displayCurrentEmail(String email) {
        emailInput.setText(email);
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void showEmailError(int errorMsgId) {
        emailInput.setError(
                getResources().getString(errorMsgId)
        );
    }

    @Override
    public void showPasswordError(int errorMsgId) {
        passwordInput.setError(
                getResources().getString(errorMsgId)
        );
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(saveButton);
    }

    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
    }


    // Нажатия
    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        presenter.onSaveButtonClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        presenter.onCancelButtonClicked();
    }


    // Внутренние методы
    private boolean hasEmail() {
        return !TextUtils.isEmpty(emailInput.getText().toString());
    }
}
