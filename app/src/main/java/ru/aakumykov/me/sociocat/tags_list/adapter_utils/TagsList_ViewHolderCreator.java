package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ViewHolder;

public class TagsList_ViewHolderCreator extends BasicMVP_ViewHolderCreator {

    public TagsList_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BasicMVP_ItemTypes.TAG_ITEM) {
            return createTagViewHolder(parent);
        }
        return super.createViewHolder(parent, viewType);
    }

    private Tag_ViewHolder createTagViewHolder(ViewGroup parent) {
        Tag_ViewHolder tagViewHolder = new Tag_ViewHolder(inflateLayout(R.layout.tags_list_item, parent));
        tagViewHolder.setItemClickListener(mItemClickListener);
        return tagViewHolder;
    }
}
