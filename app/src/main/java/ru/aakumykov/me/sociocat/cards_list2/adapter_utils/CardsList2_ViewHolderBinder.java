package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardViewHolder;

public class CardsList2_ViewHolderBinder extends BasicMVPList_ViewHolderBinder {

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
