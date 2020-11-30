package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder;

public class TagsList_ViewHolderBinder extends BasicMVP_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {
        if (holder instanceof TagViewHolder) {
            TagViewHolder tagViewHolder = (TagViewHolder) holder;
            tagViewHolder.initialize(listItem);
        }
        else
            super.bindViewHolder(holder, position, listItem);
    }
}
