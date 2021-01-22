package io.gitlab.aakumykov.sociocat.d_login_or_register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_View;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;
import io.gitlab.aakumykov.sociocat.d_login2.Login2_View;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LoginClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LogoutClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.RegisterClikcedPageEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_states.LoggedInPageState;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_states.LoggedOutPageState;
import io.gitlab.aakumykov.sociocat.register_step_1.RegisterStep1_View;

public class LoginOrRegister_View extends BasicMVVMPage_View {

    @BindView(R.id.loginButton) View loginButton;
    @BindView(R.id.logoutButton) View logoutButton;
    @BindView(R.id.registerButton) View registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateUpButton();
        setPageTitle(R.string.LOGIN_OR_REGISTER_page_title);
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
    @OnClick(R.id.loginButton)
    void onLoginButtonClicked() {
        getViewModel().onLoginButtonClicked();
    }

    @OnClick(R.id.logoutButton)
    void onLogoutButtonClicked() {
        getViewModel().onLogoutButtonClicked();
    }

    @OnClick(R.id.registerButton)
    void onSignUpButtonClicked() {
        getViewModel().onSignUpButtonClicked();
    }


    // Внутренние методы
    private LoginOrRegister_ViewModel getViewModel() {
        return (LoginOrRegister_ViewModel) mPageViewModel;
    }


    // Обработка событий
    @Override
    protected void processPageEvent(@NonNull BasicPageEvent pageEvent) {

        if (pageEvent instanceof LoginClickedEvent) {
            processLoginClickedEvent(pageEvent);
        }
        else if (pageEvent instanceof LogoutClickedEvent) {
            processLogoutClickedEvent(pageEvent);
        }
        else if (pageEvent instanceof RegisterClikcedPageEvent) {
            processRegisterClickedEvent(pageEvent);
        }
        else
            super.processPageEvent(pageEvent);
    }

    private void processLoginClickedEvent(BasicPageEvent pageEvent) {
        pageEvent.consume();
        startActivity(new Intent(this, Login2_View.class));
    }

    private void processLogoutClickedEvent(BasicPageEvent pageEvent) {
        logout();
    }

    private void processRegisterClickedEvent(BasicPageEvent pageEvent) {
        startActivity(new Intent(this, RegisterStep1_View.class));
        pageEvent.consume();
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
        ViewUtils.hide(loginButton);
        ViewUtils.hide(registerButton);

        showInfoMsg(R.string.you_already_logged_in);
        ViewUtils.show(logoutButton);
    }

    private void setLoggedOutPageState() {
        ViewUtils.show(loginButton);
        ViewUtils.show(registerButton);

        hideMessage();
        ViewUtils.hide(logoutButton);
    }
}
