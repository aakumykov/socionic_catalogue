package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;

public interface iBasic_ItemClickListener {
    void onItemLongClicked(BasicMVP_DataViewHolder basicViewHolder);
    void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder);
}
