package ru.aakumykov.me.sociocat.c_login_or_register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_View;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_events.LogoutClickedEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_events.RegisterClikcedPageEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoggedInPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoggedOutPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginSuccessPageEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithEmailAndPasswordEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithGoogleEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithVKEvent;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.register_step_1.RegisterStep1_View;
import ru.aakumykov.me.sociocat.utils.auth.GoogleAuthHelper;

public class LoginOrRegister_View extends BasicMVVMPage_View {

    @BindView(R.id.registerButton) View registerButton;
    @BindView(R.id.emailPasswordLoginInButton) View emailPasswordLoginInButton;
    @BindView(R.id.googleLoginButton) View googleLoginButton;
    @BindView(R.id.vkLoginInButton) View vkLoginInButton;
    @BindView(R.id.logoutButton) View logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateUpButton();
        setPageTitle(R.string.LOGIN_OR_REGISTER_page_title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Constants.CODE_GOOGLE_LOGIN == requestCode)
            processGoogleLoginResult(resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void setView() {
        setContentView(R.layout.login_or_register_activity);
    }

    @Override
    protected void configureView() {

    }

    @Override
    protected BasicMVVMPage_ViewModel createPageViewModel() {
        return getViewModelProvider(this)
                .get(LoginOrRegister_ViewModel.class);
    }

    @Override
    protected void onNewPageState(@NonNull BasicPageState pageState) {
        processPageState(pageState);
    }

    @Override
    protected void onNewPageEvent(@NonNull BasicPageEvent pageEvent) {
        processPageEvent(pageEvent);
    }

    @Override
    public void onUserGloballyLoggedIn() {
        getViewModel().onUserLoggedIn();
    }

    @Override
    public void onUserGloballyLoggedOut() {
        getViewModel().onUserLoggedOut();
    }



    // Реакция на нажатия
    @OnClick(R.id.registerButton)
    void onRegisterButtonClicked() {
        getViewModel().onRegisterButtonClicked();
    }

    @OnClick(R.id.emailPasswordLoginInButton)
    void onLoginViaEmailAndPasswordClicked() {
        getViewModel().onLoginWithEmailAndPasswordClicked();
    }

    @OnClick(R.id.googleLoginButton)
    void onLoginWithGoogleClicked() {
        getViewModel().onLoginWithGoogleClicked();
    }

    @OnClick(R.id.vkLoginInButton)
    void onLoginWithVKClicked() {
        getViewModel().onLoginWithVKClicked();
    }

    @OnClick(R.id.logoutButton)
    void onLogoutButtonClicked() {
        getViewModel().onLogoutButtonClicked();
    }



    // Обработка событий
    @Override
    protected void processPageEvent(@NonNull BasicPageEvent pageEvent) {

        if (pageEvent instanceof RegisterClikcedPageEvent) {
            pageEvent.consume();
            onRegisterClickedEvent();
        }
        else if (pageEvent instanceof LoginWithEmailAndPasswordEvent) {
            pageEvent.consume();
            onLoginWithEmailAndPasswordEvent();
        }
        else if (pageEvent instanceof LoginWithGoogleEvent) {
            pageEvent.consume();
            onLoginWithGoogleEvent();
        }
        else if (pageEvent instanceof LoginWithVKEvent) {
            pageEvent.consume();
            onLoginWithVKEvent();
        }
        else if (pageEvent instanceof LogoutClickedEvent) {
            pageEvent.consume();
            onLogoutClickedEvent(pageEvent);
        }
        else if (pageEvent instanceof LoginSuccessPageEvent) {
            pageEvent.consume();
            onLoginSuccessPageEvent((LoginSuccessPageEvent) pageEvent);
        }
        else {
            super.processPageEvent(pageEvent);
        }
    }

    private void onRegisterClickedEvent() {
        startActivity(new Intent(this, RegisterStep1_View.class));
    }

    private void onLogoutClickedEvent(BasicPageEvent pageEvent) {
        logout();
    }

    private void onLoginWithEmailAndPasswordEvent() {
        Intent intent = new Intent(this, Login_View.class);
        intent.setAction(Constants.ACTION_LOGIN);
        startActivityForResult(intent, Constants.CODE_LOGIN);
    }

    private void onLoginWithGoogleEvent() {
        startLoginWithGoogle();
    }

    private void onLoginWithVKEvent() {
        showToast(R.string.not_implemented_yet);
    }

    private void onLoginSuccessPageEvent(LoginSuccessPageEvent loginSuccessPageEvent) {
        showToast(R.string.LOGIN_welcome_to_socionic_catalogue);
        closePage();
    }


    // Обработка состояний
    @Override
    protected void processPageState(@NonNull BasicPageState pageState) {

        if (pageState instanceof LoggedInPageState) {
            setLoggedInPageState();
        }
        else if (pageState instanceof LoggedOutPageState) {
            setLoggedOutPageState();
        }
        else {
            super.processPageState(pageState);
        }
    }

    private void setLoggedInPageState() {
        ViewUtils.hide(registerButton);
        ViewUtils.hide(emailPasswordLoginInButton);
        ViewUtils.hide(googleLoginButton);
        ViewUtils.hide(vkLoginInButton);

        showInfoMsg(R.string.you_already_logged_in);
        ViewUtils.show(logoutButton);
    }

    private void setLoggedOutPageState() {
        ViewUtils.show(registerButton);
        ViewUtils.show(emailPasswordLoginInButton);
        ViewUtils.show(googleLoginButton);
        ViewUtils.show(vkLoginInButton);
        ViewUtils.show(logoutButton);

        hideMessage();
        ViewUtils.hide(logoutButton);
    }

    private void setLoginErrorPageState(ErrorPageState errorPageState) {
        setErrorPageState(errorPageState);
        logout();
    }


    // Другие внутренние методы
    private LoginOrRegister_ViewModel getViewModel() {
        return (LoginOrRegister_ViewModel) mPageViewModel;
    }

    private void startLoginWithGoogle() {
        Intent signInIntent = GoogleAuthHelper.getSignInIntent(this);
        startActivityForResult(signInIntent, Constants.CODE_GOOGLE_LOGIN);
    }

    private void processGoogleLoginResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                getViewModel().onLoginWithGoogleConfirmed(data);
                break;
            case RESULT_CANCELED:
                getViewModel().onLoginWithGoogleCancelled();
                break;
            default:
                getViewModel().onLoginWithGoogleUnknown(resultCode, data);
        }
    }

}
