package ru.aakumykov.me.sociocat.b_tags_list.interfaces;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_tags_list.view_holders.TagViewHolder;

public interface iTagsList_ItemClickListener extends iBasicMVP_ItemClickListener {
    void onEditTagClicked(TagViewHolder tagViewHolder);
}
