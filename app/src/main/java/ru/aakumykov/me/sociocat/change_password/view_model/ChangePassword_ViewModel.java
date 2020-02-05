package ru.aakumykov.me.sociocat.change_password.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.change_password.iChangePassword;

public class ChangePassword_ViewModel extends ViewModel {

    private iChangePassword.iPresenter presenter;

    public ChangePassword_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iChangePassword.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iChangePassword.iPresenter presenter) {
        this.presenter = presenter;
    }
}
