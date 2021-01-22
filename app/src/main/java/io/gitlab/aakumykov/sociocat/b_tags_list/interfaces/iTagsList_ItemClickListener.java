package io.gitlab.aakumykov.sociocat.b_tags_list.interfaces;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.b_tags_list.view_holders.TagViewHolder;

public interface iTagsList_ItemClickListener extends iBasicMVP_ItemClickListener {
    void onEditTagClicked(TagViewHolder tagViewHolder);
}
