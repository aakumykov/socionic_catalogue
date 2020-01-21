package ru.aakumykov.me.sociocat.user_email_change.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.user_email_change.iPage;

public class Page_ViewModel extends ViewModel {

    private iPage.iPresenter presenter;

    public Page_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iPage.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iPage.iPresenter presenter) {
        this.presenter = presenter;
    }
}