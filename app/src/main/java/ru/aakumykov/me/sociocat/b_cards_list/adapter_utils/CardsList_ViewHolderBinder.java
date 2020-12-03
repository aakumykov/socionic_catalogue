package ru.aakumykov.me.sociocat.b_cards_list.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.b_cards_list.view_holders.CardViewHolder;

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
