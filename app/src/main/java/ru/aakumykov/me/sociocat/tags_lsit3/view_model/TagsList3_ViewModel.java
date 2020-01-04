package ru.aakumykov.me.sociocat.tags_lsit3.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;

public class TagsList3_ViewModel extends ViewModel {

    private iTagsList3.iDataAdapter dataAdapter;
    private iTagsList3.iPresenter presenter;

    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }
    public void storeDataAdapter(iTagsList3.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    public iTagsList3.iDataAdapter getDataAdapter() {
        return dataAdapter;
    }

    public boolean hasPresenter() {
        return null != presenter;
    }
    public void storePresenter(iTagsList3.iPresenter presenter) {
        this.presenter = presenter;
    }
    public iTagsList3.iPresenter getPresenter() {
        return presenter;
    }
}
