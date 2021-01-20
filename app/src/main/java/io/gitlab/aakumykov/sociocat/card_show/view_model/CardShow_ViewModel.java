package io.gitlab.aakumykov.sociocat.card_show.view_model;

import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.card_show.iCardShow;

public class CardShow_ViewModel extends ViewModel {

    iCardShow.iPresenter presenter;
    iCardShow.iDataAdapter dataAdapter;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void storePresenter(iCardShow.iPresenter presenter) {
        this.presenter = presenter;
    }
    public iCardShow.iPresenter getPresenter() {
        return presenter;
    }

    public void storeDataAdapter(iCardShow.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    public iCardShow.iDataAdapter getDataAdapter() {
        return dataAdapter;
    }
}
