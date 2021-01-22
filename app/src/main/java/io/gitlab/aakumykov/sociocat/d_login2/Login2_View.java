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
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.AlreadyLoggedInEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginSuccessPageEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithEmailAndPasswordEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithGoogleEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithVKEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_states.LoginErrorPageState;
import io.gitlab.aakumykov.sociocat.login.Login_View;
import io.gitlab.aakumykov.sociocat.utils.auth.GoogleAuthHelper;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Constants.CODE_GOOGLE_LOGIN:
                processGoogleLoginResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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


    // Реакция на новые состояния
    @Override
    protected void processPageState(@NonNull BasicPageState pageState) {
        if (pageState instanceof LoginErrorPageState)
            setLoginErrorPageState((LoginErrorPageState) pageState);
        else {
            super.processPageState(pageState);
        }
    }

    private void setLoginErrorPageState(ErrorPageState errorPageState) {
        setErrorPageState(errorPageState);
        logout();
    }


    // Реакции на события
    @Override
    protected void processPageEvent(@NonNull BasicPageEvent pageEvent) {
        if (pageEvent instanceof AlreadyLoggedInEvent) {
            pageEvent.consume();
            onAlreadyLoggenInEvent();
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
        else if (pageEvent instanceof LoginSuccessPageEvent) {
            onLoginSuccessPageEvent((LoginSuccessPageEvent) pageEvent);
        }
        else {
            super.processPageEvent(pageEvent);
        }
    }

    private void onAlreadyLoggenInEvent() {
        showToast(R.string.you_already_logged_in);
        closePage();
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


    // TODO: вынести обработку Google-логина в отдельный объект
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


    private Login2_ViewModel getViewModel() {
        return (Login2_ViewModel) mPageViewModel;
    }
}
