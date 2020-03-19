package ru.aakumykov.me.sociocat.template_of_list.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList.iDataAdapter;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public class ItemsList_ViewModel extends ViewModel {

    private iItemsList.iDataAdapter dataAdapter;
    private iItemsList.iPresenter presenter;

    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }
    public void storeDataAdapter(iItemsList.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    public iItemsList.iDataAdapter getDataAdapter() {
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
