package io.gitlab.aakumykov.sociocat.d_login2;

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
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.AlreadyLoggedInEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithEmailAndPasswordEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithGoogleEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithVKEvent;
import io.gitlab.aakumykov.sociocat.login.Login_View;

public class Login2_View extends BasicMVVMPage_View {


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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateUpButton();
        setPageTitle(R.string.LOGIN_page_title);
    }

    @Override
    protected void setView() {
        setContentView(R.layout.login2_activity);
    }

    @Override
    protected void configureView() {

    }

    @Override
    protected BasicMVVMPage_ViewModel createPageViewModel() {
        return getViewModelProvider(this).get(Login2_ViewModel.class);
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
        if (pageEvent instanceof AlreadyLoggedInEvent) {
            showToast(R.string.you_already_logged_in);
            pageEvent.consume();
            closePage();
        }
        else if (pageEvent instanceof LoginWithEmailAndPasswordEvent) {
            proceedLoginWithEmailAndPassword();
            pageEvent.consume();
        }
        else if (pageEvent instanceof LoginWithGoogleEvent) {
            processLoginViaGoogle();
            pageEvent.consume();
        }
        else if (pageEvent instanceof LoginWithVKEvent) {
            showToast(R.string.not_implemented_yet);
            pageEvent.consume();
        }
        else {
            super.processPageEvent(pageEvent);
        }
    }


    private void proceedLoginWithEmailAndPassword() {
        Intent intent = new Intent(this, Login_View.class);
        intent.setAction(Constants.ACTION_LOGIN);
        startActivityForResult(intent, Constants.CODE_LOGIN);
    }

    private void processLoginViaGoogle() {

    }


    private Login2_ViewModel getViewModel() {
        return (Login2_ViewModel) mPageViewModel;
    }
}
