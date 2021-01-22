package io.gitlab.aakumykov.sociocat.d_login2;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.singletons.AuthSingleton;

public class Login2_ViewModel extends BasicMVVMPage_ViewModel {

    private AuthSingleton mAuthSingleton = AuthSingleton.getInstance();

    @Override
    protected void onColdStart() {


    }
}
