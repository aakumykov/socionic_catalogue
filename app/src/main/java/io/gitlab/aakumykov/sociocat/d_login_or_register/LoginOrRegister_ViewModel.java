package io.gitlab.aakumykov.sociocat.d_login_or_register;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.LoginClickedEvent;
import io.gitlab.aakumykov.sociocat.d_login_or_register.page_events.RegisterClikcedPageEvent;

public class LoginOrRegister_ViewModel extends BasicMVVMPage_ViewModel {

    @Override
    protected void onColdStart() {

    }

    public void onSignInButtonClicked() {
        risePageEvent(new LoginClickedEvent());
    }

    public void onSignUpButtonClicked() {
        risePageEvent(new RegisterClikcedPageEvent());
    }
}
