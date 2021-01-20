package io.gitlab.aakumykov.sociocat.b_cards_list.adapter_utils;

import android.view.ViewGroup;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import io.gitlab.aakumykov.sociocat.b_cards_list.view_holders.CardViewHolder_Feed;
import io.gitlab.aakumykov.sociocat.b_cards_list.view_holders.CardViewHolder_Grid;
import io.gitlab.aakumykov.sociocat.b_cards_list.view_holders.CardViewHolder_List;

public class CardsList_ViewHolderCreator extends BasicMVPList_ViewHolderCreator {

    public CardsList_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }


    @Override
    public BasicMVPList_DataViewHolder createViewHolder4feedMode(ViewGroup parent) {
        return new CardViewHolder_Feed(inflateItemView(parent, R.layout.cards_list_card_item_feed));
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4listMode(ViewGroup parent) {
        return new CardViewHolder_List(inflateItemView(parent, R.layout.cards_list_card_item_list));
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4gridMode(ViewGroup parent) {
        return new CardViewHolder_Grid(inflateItemView(parent, R.layout.cards_list_card_item_grid));
    }
}
