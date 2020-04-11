package ru.aakumykov.me.sociocat.template_of_list.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.template_of_list.iTemplateOfList;

public class TemplateOfList_ViewModel extends ViewModel {

    private iTemplateOfList.iDataAdapter dataAdapter;
    private iTemplateOfList.iPresenter presenter;

    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }
    public void storeDataAdapter(iTemplateOfList.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }
    public iTemplateOfList.iDataAdapter getDataAdapter() {
        return dataAdapter;
    }

    public boolean hasPresenter() {
        return null != presenter;
    }
    public void storePresenter(iTemplateOfList.iPresenter presenter) {
        this.presenter = presenter;
    }
    public iTemplateOfList.iPresenter getPresenter() {
        return presenter;
    }
}
