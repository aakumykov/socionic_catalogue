package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.exceptions.UnknownViewModeException;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.FeedViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.GridViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_Feed;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_Grid;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder_List;

public class TagsList_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public TagsList_ViewHolderCreator(iTagsList_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, BasicViewMode viewMode) {
        if (BasicMVP_ItemTypes.TAG_ITEM == viewType) {
            return createTagViewHolder(parent, viewMode);
        }
        return super.createViewHolder(parent, viewType, viewMode);
    }

    private TagViewHolder createTagViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        TagViewHolder tagViewHolder = prepareTagViewHolder(parent, viewMode);

        tagViewHolder.setItemClickListener(mItemClickListener);

        return tagViewHolder;
    }

    private TagViewHolder prepareTagViewHolder(ViewGroup parent, BasicViewMode viewMode) {
        int layoutId = getLayoutForViewMode(viewMode);
        View itemView = inflateLayout(layoutId, parent);

        TagViewHolder tagViewHolder;

        if (viewMode instanceof ListViewMode)
            tagViewHolder = new TagViewHolder_List(itemView);
        else if (viewMode instanceof GridViewMode)
            tagViewHolder = new TagViewHolder_Grid(itemView);
        else if (viewMode instanceof FeedViewMode)
            tagViewHolder = new TagViewHolder_Feed(itemView);
        else
            throw new UnknownViewModeException(viewMode);

        return tagViewHolder;
    }

    private int getLayoutForViewMode(BasicViewMode viewMode) {
        if (viewMode instanceof ListViewMode)
            return R.layout.tags_item_list;
        else if (viewMode instanceof GridViewMode)
            return R.layout.tags_item_grid;
        else if (viewMode instanceof FeedViewMode)
            return R.layout.tags_item_feed;
        else
            throw new UnknownViewModeException(viewMode);
    }


}
