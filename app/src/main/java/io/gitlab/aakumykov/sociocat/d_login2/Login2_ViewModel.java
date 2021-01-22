package io.gitlab.aakumykov.sociocat.d_login2;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.AlreadyLoggedInEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithEmailAndPasswordEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithGoogleEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithVKEvent;
import io.gitlab.aakumykov.sociocat.singletons.AuthSingleton;

public class Login2_ViewModel extends BasicMVVMPage_ViewModel {

    @Override
    protected void onColdStart() {
        if (AuthSingleton.isLoggedIn())
            risePageEvent(new AlreadyLoggedInEvent());
    }

    public void onLoginWithEmailAndPasswordClicked() {
        risePageEvent(new LoginWithEmailAndPasswordEvent());
    }

    public void onLoginWithGoogleClicked() {
        risePageEvent(new LoginWithGoogleEvent());
    }

    public void onLoginWithVKClicked() {
        risePageEvent(new LoginWithVKEvent());
    }
}
