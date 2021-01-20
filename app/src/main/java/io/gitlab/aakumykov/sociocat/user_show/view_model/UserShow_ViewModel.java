package io.gitlab.aakumykov.sociocat.user_show.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.user_show.iUserShow;

public class UserShow_ViewModel extends ViewModel {

    private iUserShow.iPresenter presenter;

    public UserShow_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iUserShow.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iUserShow.iPresenter presenter) {
        this.presenter = presenter;
    }
}
