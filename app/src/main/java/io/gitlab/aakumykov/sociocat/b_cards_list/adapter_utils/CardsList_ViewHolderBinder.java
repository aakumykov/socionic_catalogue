package io.gitlab.aakumykov.sociocat.b_cards_list.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.b_cards_list.view_holders.CardViewHolder;

public class CardsList_ViewHolderBinder extends BasicMVPList_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVPList_ListItem listItem) {
        if (holder instanceof CardViewHolder) {
            CardViewHolder viewHolder = (CardViewHolder) holder;
            viewHolder.initialize(listItem);
        }
        else {
            super.bindViewHolder(holder, position, listItem);
        }
    }
}
