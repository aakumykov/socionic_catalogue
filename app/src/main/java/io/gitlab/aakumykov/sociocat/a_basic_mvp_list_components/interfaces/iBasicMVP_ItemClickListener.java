package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces;


import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;

public interface iBasicMVP_ItemClickListener {
    void onItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder);
    void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder);
    void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder);
}
