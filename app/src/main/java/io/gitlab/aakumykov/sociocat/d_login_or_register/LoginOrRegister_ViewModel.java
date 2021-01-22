package io.gitlab.aakumykov.sociocat.d_login_or_register;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LoginClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LogoutClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.RegisterClikcedPageEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_states.LoggedInPageState;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_states.LoggedOutPageState;

public class LoginOrRegister_ViewModel extends BasicMVVMPage_ViewModel {

    @Override
    protected void onColdStart() {
        // Аутентификацию здесь проверять не нужно, так как она обрабатывается
        // методами onUserGloballyLoggedIn(), onUserGloballyLoggedOut()
    }


    public void onLoginButtonClicked() {
        risePageEvent(new LoginClickedEvent());
    }

    public void onLogoutButtonClicked() {
        risePageEvent(new LogoutClickedEvent());
    }

    public void onSignUpButtonClicked() {
        risePageEvent(new RegisterClikcedPageEvent());
    }


    public void onUserLoggedIn() {
        setLoggedInPageState();
    }

    public void onUserLoggedOut() {
        setLoggedOutPageState();
    }


    private void setLoggedInPageState() {
        setPageState(new LoggedInPageState());
    }

    private void setLoggedOutPageState() {
        setPageState(new LoggedOutPageState());
    }
}
