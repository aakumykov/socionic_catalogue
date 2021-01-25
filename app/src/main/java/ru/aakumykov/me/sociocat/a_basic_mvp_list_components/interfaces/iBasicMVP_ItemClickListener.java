package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;

public interface iBasicMVP_ItemClickListener {
    void onItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder);
    void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder);
    void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder);
}
