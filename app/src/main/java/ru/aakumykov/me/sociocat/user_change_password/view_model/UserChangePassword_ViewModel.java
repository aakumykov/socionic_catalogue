package ru.aakumykov.me.sociocat.user_change_password.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.user_change_password.iUserChangePassword;

public class UserChangePassword_ViewModel extends ViewModel {

    private iUserChangePassword.iPresenter presenter;

    public UserChangePassword_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iUserChangePassword.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iUserChangePassword.iPresenter presenter) {
        this.presenter = presenter;
    }
}
