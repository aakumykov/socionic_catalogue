package ru.aakumykov.me.sociocat.register_step_2.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.register_step_2.iRegisterStep2;
import ru.aakumykov.me.sociocat.template_of_page.iPage;

public class RegisterStep2_ViewModel extends ViewModel {

    private iRegisterStep2.Presenter presenter;

    public RegisterStep2_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public iRegisterStep2.Presenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iRegisterStep2.Presenter presenter) {
        this.presenter = presenter;
    }
}
