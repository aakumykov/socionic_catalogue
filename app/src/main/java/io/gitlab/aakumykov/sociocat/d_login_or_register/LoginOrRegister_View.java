package io.gitlab.aakumykov.sociocat.d_login_or_register;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.OnClick;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_View;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;
import io.gitlab.aakumykov.sociocat.d_login2.Login2_View;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LoginClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.RegisterClikcedPageEvent;
import io.gitlab.aakumykov.sociocat.register_step_1.RegisterStep1_View;

public class LoginOrRegister_View extends BasicMVVMPage_View {

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

    }

    @Override
    public void onUserGloballyLoggedOut() {

    }

    @Override
    protected void processPageState(@NonNull BasicPageState pageState) {
        super.processPageState(pageState);
    }

    @Override
    protected void processPageEvent(@NonNull BasicPageEvent pageEvent) {

        if (pageEvent instanceof LoginClickedEvent)
            processLoginClickedEvent(pageEvent);
        else if (pageEvent instanceof RegisterClikcedPageEvent)
            processRegisterClickedEvent(pageEvent);
        else
            super.processPageEvent(pageEvent);
    }

    @OnClick(R.id.signInButton)
    void onSignInButtonClicked() {
        getViewModel().onSignInButtonClicked();
    }

    @OnClick(R.id.signUpButton)
    void onSignUpButtonClicked() {
        getViewModel().onSignUpButtonClicked();
    }


    private LoginOrRegister_ViewModel getViewModel() {
        return (LoginOrRegister_ViewModel) mPageViewModel;
    }


    private void processLoginClickedEvent(BasicPageEvent pageEvent) {
        startActivity(new Intent(this, Login2_View.class));
        pageEvent.consume();
    }

    private void processRegisterClickedEvent(BasicPageEvent pageEvent) {
        startActivity(new Intent(this, RegisterStep1_View.class));
        pageEvent.consume();
    }


}
