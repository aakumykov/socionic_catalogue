package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;

public class CardsList2_ViewHolderBinder extends BasicMVP_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {
        super.bindViewHolder(holder, position, listItem);
    }

    @Override
    public void setClickListener(iBasicMVP_ItemClickListener clickListener) {
        super.setClickListener(clickListener);
    }
}
