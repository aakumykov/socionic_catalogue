package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ViewHolder;

public interface iBasicMVP_ItemClickListener {
    void onItemClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onItemLongClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder);
}
