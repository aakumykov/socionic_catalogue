package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ViewHolder;

public class TagsList_ViewHolderBinder extends BasicMVP_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVP_ListItem listItem) {
        if (holder instanceof Tag_ViewHolder) {
            Tag_ViewHolder tagViewHolder = (Tag_ViewHolder) holder;
            tagViewHolder.fillWithData(listItem);
        }
        else
            super.bindViewHolder(holder, position, listItem);
    }
}
