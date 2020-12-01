package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_Feed;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_Grid;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder_List;

public class CardsList2_ViewHolderCreator extends BasicMVPList_ViewHolderCreator {

    public CardsList2_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }


    @Override
    public BasicMVPList_DataViewHolder createViewHolder4feedMode(ViewGroup parent) {
        return new CardViewHolder_Feed(inflateItemView(parent, R.layout.cards_list2_card_item_feed));
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4listMode(ViewGroup parent) {
        return new CardViewHolder_List(inflateItemView(parent, R.layout.cards_list2_card_item_list));
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4gridMode(ViewGroup parent) {
        return new CardViewHolder_Grid(inflateItemView(parent, R.layout.cards_list2_card_item_grid));
    }
}
