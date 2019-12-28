package ru.aakumykov.me.sociocat.page_template.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.page_template.iPage;

public class Page_ViewModel extends ViewModel {

    private ru.aakumykov.me.sociocat.page_template.iPage.iPresenter presenter;

    public Page_ViewModel() {
    }

    public boolean hasPresenter() {
        return null != presenter;
    }

    public ru.aakumykov.me.sociocat.page_template.iPage.iPresenter getPresenter() {
        return presenter;
    }

    public void storePresenter(iPage.iPresenter presenter) {
        this.presenter = presenter;
    }
}
