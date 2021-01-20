package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components;

public interface iBasicMVPList_ViewModel {

    boolean hasDataAdapter();
    Object getDataAdapter();
    void setDataAdapter(Object dataAdapter);

    boolean hasPresenter();
    Object getPresenter();
    void setPresenter(Object presenter);
}
