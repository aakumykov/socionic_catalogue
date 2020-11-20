package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_Feed;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_Grid;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_List;

public class TagsList_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public TagsList_ViewHolderCreator(iTagsList_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }


    @Override
    public BasicMVP_DataViewHolder createViewHolder4listMode(ViewGroup parent) {
        return new TagViewHolder_List(inflateItemView(parent, R.layout.tags_item_list));
    }

    @Override
    public BasicMVP_DataViewHolder createViewHolder4feedMode(ViewGroup parent) {
        return new TagViewHolder_Feed(inflateItemView(parent, R.layout.tags_item_feed));
    }

    @Override
    public BasicMVP_DataViewHolder createViewHolder4gridMode(ViewGroup parent) {
        return new TagViewHolder_Grid(inflateItemView(parent, R.layout.tags_item_grid));
    }
}
