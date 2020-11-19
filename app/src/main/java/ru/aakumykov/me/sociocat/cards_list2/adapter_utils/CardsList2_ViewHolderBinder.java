package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInFeed_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInGrid_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.CardInList_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list2.view_holders.Card_ViewHolder;

public class CardsList2_ViewHolderBinder extends BasicMVP_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {

        if (holder instanceof Card_ViewHolder)
        {
            Card_ViewHolder cardViewHolder = (Card_ViewHolder) holder;

            if (cardViewHolder instanceof CardInList_ViewHolder) {
                ((CardInList_ViewHolder) holder).fillWithData(listItem);
            }
            else if (cardViewHolder instanceof CardInGrid_ViewHolder) {
                ((CardInGrid_ViewHolder) holder).fillWithData(listItem);
            }
            else if (cardViewHolder instanceof CardInFeed_ViewHolder) {
                ((CardInFeed_ViewHolder) holder).fillWithData(listItem);
            }
            else
                throw new RuntimeException("Неизвестный cardViewHolder: "+cardViewHolder);
        }
        else {
            super.bindViewHolder(holder, position, listItem);
        }
    }
}
