package ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ThrobberViewHolder;

public class BasicMVP_ViewHolderCreator {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVP_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case BasicMVP_ItemTypes.LOADMORE_ITEM:
                return createLoadmoreViewHolder(parent);

            case BasicMVP_ItemTypes.THROBBER_ITEM:
                return new BasicMVP_ThrobberViewHolder(inflateLayout(R.layout.basic_list_item_throbber, parent));

            default:
                throw new RuntimeException("Неизвестный viewType: "+viewType);
        }
    }

    private BasicMVP_LoadmoreViewHolder createLoadmoreViewHolder(ViewGroup parent) {
        BasicMVP_LoadmoreViewHolder loadmoreViewHolder =
                new BasicMVP_LoadmoreViewHolder(inflateLayout(R.layout.basic_list_item_loadmore, parent));

        loadmoreViewHolder.setItemClickListener(mItemClickListener);

        return loadmoreViewHolder;
    }

    protected View inflateLayout(int layoutId, ViewGroup parent) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layoutId, parent, false);
    }
}