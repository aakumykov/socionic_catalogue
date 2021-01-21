package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_model;


import androidx.lifecycle.ViewModel;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVPList_ViewModel;

public class BasicMVPList_ViewModel extends ViewModel implements iBasicMVPList_ViewModel
{
    private Object dataAdapter;
    private Object presenter;

    @Override
    public boolean hasDataAdapter() {
        return null != dataAdapter;
    }

    @Override
    public Object getDataAdapter() {
        return dataAdapter;
    }

    @Override
    public void setDataAdapter(Object dataAdapter) {
        this.dataAdapter = dataAdapter;
    }


    @Override
    public boolean hasPresenter() {
        return null != presenter;
    }

    @Override
    public Object getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(Object presenter) {
        this.presenter = presenter;
    }
}
