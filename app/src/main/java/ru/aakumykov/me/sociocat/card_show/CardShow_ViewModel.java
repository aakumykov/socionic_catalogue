package ru.aakumykov.me.sociocat.card_show;

import androidx.lifecycle.ViewModel;

public class CardShow_ViewModel extends ViewModel {

    iCardShow.iPresenter presenter;

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
}
