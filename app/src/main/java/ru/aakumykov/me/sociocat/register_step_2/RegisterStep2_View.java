package ru.aakumykov.me.sociocat.register_step_2;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.register_step_1.RegisterStep1_Presenter;
import ru.aakumykov.me.sociocat.register_step_1.view_model.RegisterStep1_ViewModel;
import ru.aakumykov.me.sociocat.register_step_1.view_model.RegisterStep1_ViewModelFactory;
import ru.aakumykov.me.sociocat.register_step_2.view_model.RegisterStep2_ViewModel;
import ru.aakumykov.me.sociocat.register_step_2.view_model.RegisterStep2_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep2_View extends BaseView implements iRegisterStep2.View {

    @BindView(R.id.userMessage) TextView userMessage;
    @BindView(R.id.userNameInput) EditText userNameInput;
    @BindView(R.id.nameThrobber) ProgressBar nameThrobber;
    @BindView(R.id.password1Input) EditText password1Input;
    @BindView(R.id.password2Input) EditText password2Input;
    @BindView(R.id.saveButton) Button saveButton;

    private iRegisterStep2.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_page_title);
        activateUpButton();

        RegisterStep2_ViewModel viewModel = new ViewModelProvider(this, new RegisterStep2_ViewModelFactory()).get(RegisterStep2_ViewModel.class);

        if (viewModel.hasPresenter())
            presenter = viewModel.getPresenter();
        else {
            presenter = new RegisterStep2_Presenter();
            viewModel.storePresenter(presenter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.isVirgin())
            presenter.processInputIntent(getIntent());
        else
            presenter.onConfigChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    // Интерфейсные методы
    @Override
    public void hideUserMessage() {
        MyUtils.hide(userMessage);
    }

    @Override
    public void setState(iRegisterStep2.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iRegisterStep2.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case PROGRESS:
                showProgressMessage(messageId);
                break;

            case SUCCESS:
                hideProgressMessage();
                showToast(messageId);
                break;

            case ERROR:
                showErrorMsg(messageId, messageDetails);
                break;
        }
    }

    @Override
    public String getUserName() {
        return userNameInput.getText().toString();
    }

    @Override
    public String getPassword1() {
        return password1Input.getText().toString();
    }

    @Override
    public String getPassword2() {
        return password2Input.getText().toString();
    }

    @Override
    public void showUserNameError(int msgId) {
        String message = getResources().getString(msgId);
        userNameInput.setError(message);
    }

    @Override
    public void showPassword1Error(int msgId) {
        String message = getResources().getString(msgId);
        password1Input.setError(message);
    }

    @Override
    public void showPassword2Error(int msgId) {
        String message = getResources().getString(msgId);
        password2Input.setError(message);
    }

    @Override
    public void disableForm() {
        MyUtils.disable(password1Input);
        MyUtils.disable(password2Input);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(password1Input);
        MyUtils.enable(password2Input);
        MyUtils.enable(saveButton);
    }

    @Override
    public void showNameThrobber() {
        MyUtils.disable(userNameInput);
        MyUtils.show(nameThrobber);
    }

    @Override
    public void hideNameThrobber() {
        MyUtils.enable(userNameInput);
        MyUtils.hide(nameThrobber);
    }

    @Override
    public void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }


    // Нажатия
    @OnClick(R.id.saveButton)
    void onSendButtonClicked() {
        presenter.processRegistration(getIntent());
    }

}
