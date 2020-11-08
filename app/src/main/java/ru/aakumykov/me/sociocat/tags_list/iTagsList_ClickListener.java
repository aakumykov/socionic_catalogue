package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;

public interface iTagsList_ClickListener extends iBasicMVP_ItemClickListener {
    void onTagClicked(Tag_ViewHolder tagViewHolder);
}
