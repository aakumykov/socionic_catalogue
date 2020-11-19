package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.list_parts.CardInList_ViewHolder;

public class CardsList2_ViewHolderBinder extends BasicMVP_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {
        if (holder instanceof CardInList_ViewHolder) {
            CardInList_ViewHolder viewHolder = (CardInList_ViewHolder) holder;
            viewHolder.fillWithData(listItem);
        }
        else {
            super.bindViewHolder(holder, position, listItem);
        }
    }
}
