package ru.aakumykov.me.sociocat.template_of_page.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.template_of_page.iPage;

public class Page_ViewModel extends ViewModel {

    private ru.aakumykov.me.sociocat.template_of_page.iPage.iPresenter presenter;

    public Page_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public ru.aakumykov.me.sociocat.template_of_page.iPage.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iPage.iPresenter presenter) {
        this.presenter = presenter;
    }
}
