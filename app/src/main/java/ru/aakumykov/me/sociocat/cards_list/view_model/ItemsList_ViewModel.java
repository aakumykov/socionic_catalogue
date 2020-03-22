package ru.aakumykov.me.sociocat.cards_list.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.cards_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList.iDataAdapter;

public class ItemsList_ViewModel extends ViewModel {

    private iDataAdapter dataAdapter;
    private iItemsList.iPresenter presenter;

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
    public void storePresenter(iItemsList.iPresenter presenter) {
        this.presenter = presenter;
    }
    public iItemsList.iPresenter getPresenter() {
        return presenter;
    }
}
