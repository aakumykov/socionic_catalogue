package ru.aakumykov.me.sociocat.b_tags_list.adapter_utils;

import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.b_tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.b_tags_list.view_holders.TagViewHolder_List;

public class TagsList_ViewHolderCreator extends BasicMVPList_ViewHolderCreator {

    public TagsList_ViewHolderCreator(iTagsList_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }


    @Override
    public BasicMVPList_DataViewHolder createViewHolder4listMode(ViewGroup parent) {
        return new TagViewHolder_List(inflateItemView(parent, R.layout.tags_list_item));
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4feedMode(ViewGroup parent) {
        return createViewHolder4listMode(parent);
    }

    @Override
    public BasicMVPList_DataViewHolder createViewHolder4gridMode(ViewGroup parent) {
        return createViewHolder4listMode(parent);
    }
}
