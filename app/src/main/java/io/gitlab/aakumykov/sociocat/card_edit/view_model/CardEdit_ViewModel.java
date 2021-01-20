package io.gitlab.aakumykov.sociocat.card_edit.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.card_edit.iCardEdit;

public class CardEdit_ViewModel extends ViewModel {

    private iCardEdit.Presenter presenter;

    public void storePresenter(iCardEdit.Presenter presenter) {
        this.presenter = presenter;
    }

    public iCardEdit.Presenter getPresenter() {
        return presenter;
    }

    public boolean hasPresenter() {
        return null != presenter;
    }
}
