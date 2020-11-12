package ru.aakumykov.me.sociocat.tags_list.interfaces;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ViewHolder;

public interface iTagsList_ClickListener extends iBasicMVP_ItemClickListener {
    void onEditTagClicked(Tag_ViewHolder tagViewHolder);
}
