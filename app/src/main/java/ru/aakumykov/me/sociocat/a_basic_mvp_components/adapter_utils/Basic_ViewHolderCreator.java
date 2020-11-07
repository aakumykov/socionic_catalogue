package ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.data_types.ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasic_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.Basic_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.Basic_ThrobberViewHolder;

public class Basic_ViewHolderCreator {

    protected iBasic_ItemClickListener mItemClickListener;

    public Basic_ViewHolderCreator(iBasic_ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case ItemTypes.LOADMORE_ITEM:
                return createLoadmoreViewHolder(parent);

            case ItemTypes.THROBBER_ITEM:
                return new Basic_ThrobberViewHolder(inflateLayout(R.layout.basic_list_item_throbber, parent));

            default:
                throw new RuntimeException("Неизвестный viewType: "+viewType);
        }
    }

    private Basic_LoadmoreViewHolder createLoadmoreViewHolder(ViewGroup parent) {
        Basic_LoadmoreViewHolder loadmoreViewHolder =
                new Basic_LoadmoreViewHolder(inflateLayout(R.layout.basic_list_item_loadmore, parent));

        loadmoreViewHolder.setItemClickListener(mItemClickListener);

        return loadmoreViewHolder;
    }

    protected View inflateLayout(int layoutId, ViewGroup parent) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layoutId, parent, false);
    }
}