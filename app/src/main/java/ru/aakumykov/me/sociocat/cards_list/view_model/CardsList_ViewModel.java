package ru.aakumykov.me.sociocat.cards_list.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.iCardsList.iDataAdapter;

public class CardsList_ViewModel extends ViewModel {

    private iDataAdapter dataAdapter;
    private iCardsList.iPresenter presenter;

    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }
    public void storeDataAdapter(iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    public iDataAdapter getDataAdapter() {
        return dataAdapter;
    }

    public boolean hasPresenter() {
        return null != presenter;
    }
    public void storePresenter(iCardsList.iPresenter presenter) {
        this.presenter = presenter;
    }
    public iCardsList.iPresenter getPresenter() {
        return presenter;
    }
}
