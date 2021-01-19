package io.gitlab.aakumykov.sociocat.c_tags_list.adapter_utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.c_tags_list.view_holders.TagViewHolder;

public class TagsList_ViewHolderBinder extends BasicMVPList_ViewHolderBinder {

    @Override
    public void bindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, BasicMVPList_ListItem listItem) {
        if (holder instanceof TagViewHolder) {
            TagViewHolder tagViewHolder = (TagViewHolder) holder;
            tagViewHolder.initialize(listItem);
        }
        else
            super.bindViewHolder(holder, position, listItem);
    }
}
