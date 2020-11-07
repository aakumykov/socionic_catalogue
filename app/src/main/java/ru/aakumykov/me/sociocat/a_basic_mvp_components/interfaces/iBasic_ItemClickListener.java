package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.Basic_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.Basic_ViewHolder;

public interface iBasic_ItemClickListener {
    void onItemLongClicked(Basic_DataViewHolder basicViewHolder);
    void onLoadMoreClicked(Basic_ViewHolder basicViewHolder);
}
