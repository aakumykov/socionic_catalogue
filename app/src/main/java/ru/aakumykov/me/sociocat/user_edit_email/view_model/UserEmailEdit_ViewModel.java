package ru.aakumykov.me.sociocat.user_edit_email.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.user_edit_email.iUserEditEmail;

public class UserEmailEdit_ViewModel extends ViewModel {

    private iUserEditEmail.iPresenter presenter;

    public UserEmailEdit_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iUserEditEmail.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iUserEditEmail.iPresenter presenter) {
        this.presenter = presenter;
    }
}
