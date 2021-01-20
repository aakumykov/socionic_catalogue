package io.gitlab.aakumykov.sociocat.login.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.login.iLogin;

public class Login_ViewModel extends ViewModel {

    private iLogin.Presenter presenter;

    public Login_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iLogin.Presenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iLogin.Presenter presenter) {
        this.presenter = presenter;
    }
}
