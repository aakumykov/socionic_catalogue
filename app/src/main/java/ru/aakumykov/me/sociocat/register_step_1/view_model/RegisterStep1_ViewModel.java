package ru.aakumykov.me.sociocat.register_step_1.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.register_step_1.iRegisterStep1;
import ru.aakumykov.me.sociocat.template_of_page.iPage;

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
