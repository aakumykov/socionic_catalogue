package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVP_ViewHolder;

public interface iBasicMVP_ItemClickListener {
    void onItemClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onItemLongClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder);
}
