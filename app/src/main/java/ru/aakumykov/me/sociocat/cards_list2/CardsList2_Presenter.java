package ru.aakumykov.me.sociocat.cards_list2;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ViewHolder;

public class CardsList2_Presenter extends BasicMVP_Presenter {

    public CardsList2_Presenter(iSortingMode defaultSortingMode) {
        super(defaultSortingMode);
    }

    @Override
    public void unbindViews() {

    }

    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {
        return null;
    }

    @Override
    protected void onRefreshRequested() {

    }

    @Override
    public void onItemClicked(BasicMVP_DataViewHolder basicDataViewHolder) {

    }

    @Override
    public void onItemLongClicked(BasicMVP_DataViewHolder basicDataViewHolder) {

    }

    @Override
    public void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder) {

    }
}
