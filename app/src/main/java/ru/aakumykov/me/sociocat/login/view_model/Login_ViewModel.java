package ru.aakumykov.me.sociocat.login.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.login.iLogin;
import ru.aakumykov.me.sociocat.template_of_page.iPage;

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
