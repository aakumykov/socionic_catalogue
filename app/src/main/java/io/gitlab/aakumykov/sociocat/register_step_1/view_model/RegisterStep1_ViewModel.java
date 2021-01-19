package io.gitlab.aakumykov.sociocat.register_step_1.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.register_step_1.iRegisterStep1;

public class RegisterStep1_ViewModel extends ViewModel {

    private iRegisterStep1.Presenter presenter;

    public RegisterStep1_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iRegisterStep1.Presenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iRegisterStep1.Presenter presenter) {
        this.presenter = presenter;
    }
}
