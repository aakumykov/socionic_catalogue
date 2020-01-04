package ru.aakumykov.me.sociocat.user_edit.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.user_edit.iUserEdit;

public class UserEdit_ViewModel extends ViewModel {

    private iUserEdit.iPresenter presenter;

    public UserEdit_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iUserEdit.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iUserEdit.iPresenter presenter) {
        this.presenter = presenter;
    }
}
