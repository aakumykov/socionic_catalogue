package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;

public interface iSelectionCommandsListener {
    void onSelectItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder);
    void onSelectAllClicked();
    void onClearSelectionClicked();
    void onInvertSelectionClicked();
}
