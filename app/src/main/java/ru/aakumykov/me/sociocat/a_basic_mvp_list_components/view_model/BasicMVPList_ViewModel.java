package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_model;


import androidx.lifecycle.ViewModel;

public class BasicMVPList_ViewModel extends ViewModel
{
    private Object dataAdapter;
    private Object presenter;

    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }
    public Object getDataAdapter() {
        return dataAdapter;
    }
    public void setDataAdapter(Object dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    public boolean hasPresenter() {
        return null != presenter;
    }
    public Object getPresenter() {
        return presenter;
    }
    public void setPresenter(Object presenter) {
        this.presenter = presenter;
    }
}
