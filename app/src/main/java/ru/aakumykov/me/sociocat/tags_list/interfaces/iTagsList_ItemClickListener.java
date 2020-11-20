package ru.aakumykov.me.sociocat.tags_list.interfaces;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder;

public interface iTagsList_ItemClickListener extends iBasicMVP_ItemClickListener {
    void onEditTagClicked(TagViewHolder tagViewHolder);
}
