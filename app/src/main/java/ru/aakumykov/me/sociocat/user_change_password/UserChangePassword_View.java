package ru.aakumykov.me.sociocat.user_change_password;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModel;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserChangePassword_View extends BaseView implements iUserChangePassword.iView {

    @Password
    @BindView(R.id.currentPasswordInput)
    EditText currentPasswordInput;

    @Password
    @BindView(R.id.newPasswordInput)
    EditText newPasswordInput;

    @ConfirmPassword
    @BindView(R.id.newPasswordConfirmationInput)
    EditText newPasswordConfirmationInput;

    @BindView(R.id.saveButton)
    Button saveButton;

    @BindView(R.id.cancelButton)
    Button cancelButton;

    private iUserChangePassword.iPresenter presenter;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_password_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_CHANGE_PASSWORD_page_title);
        activateUpButton();

        UserChangePassword_ViewModel viewModel = new ViewModelProvider(this, new UserChangePassword_ViewModelFactory())
                .get(UserChangePassword_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserChangePassword_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

/*
        if (presenter.hasItem())
            presenter.onConfigChanged();
        else
            presenter.onFirstOpen(getIntent());
*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        presenter.onUserLoggedOut();
    }


    // Нажатия
    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {

    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        
    }

    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case PROGRESS:
                disableForm();
                showProgressMessage(messageId);
                break;

            case SUCCESS:
                showInfoMsg(messageId);
                break;

            case ERROR:
                enableForm();
                showErrorMsg(messageId, messageDetails);
                break;
        }
    }

    @Override
    public String getCurrentPassword() {
        return currentPasswordInput.getText().toString();
    }

    private void enableForm() {
        MyUtils.enable(currentPasswordInput);
        MyUtils.enable(newPasswordInput);
        MyUtils.enable(newPasswordConfirmationInput);
        MyUtils.enable(saveButton);
    }

    private void disableForm() {
        MyUtils.disable(currentPasswordInput);
        MyUtils.disable(newPasswordInput);
        MyUtils.disable(newPasswordConfirmationInput);
        MyUtils.disable(saveButton);
    }
}
