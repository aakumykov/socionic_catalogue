package io.gitlab.aakumykov.sociocat.reset_password_step1.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.reset_password_step1.iResetPasswordStep1;

public class ResetPasswordStep1_ViewModel extends ViewModel {

    private iResetPasswordStep1.Presenter presenter;

    public ResetPasswordStep1_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iResetPasswordStep1.Presenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iResetPasswordStep1.Presenter presenter) {
        this.presenter = presenter;
    }
}
