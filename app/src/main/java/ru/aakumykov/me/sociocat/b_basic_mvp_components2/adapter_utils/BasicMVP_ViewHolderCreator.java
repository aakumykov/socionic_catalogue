package ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.RecyclerViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ThrobberViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

public class BasicMVP_ViewHolderCreator {

    protected iBasicMVP_ItemClickListener mItemClickListener;


    public BasicMVP_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, BasicViewMode viewMode) {

        switch (viewType) {
            case BasicMVP_ItemTypes.LOADMORE_ITEM:
                return createLoadmoreViewHolder(parent, viewMode);

            case BasicMVP_ItemTypes.THROBBER_ITEM:
                return createThrobberViewHolder(parent, viewMode);

            default:
                throw new RuntimeException("Неизвестный viewType: "+viewType);
        }
    }

    private BasicMVP_LoadmoreViewHolder createLoadmoreViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        BasicMVP_LoadmoreViewHolder loadmoreViewHolder =
                new BasicMVP_LoadmoreViewHolder(inflateLayout(R.layout.basic_list_item_loadmore, parent));

        loadmoreViewHolder.setItemClickListener(mItemClickListener);

        RecyclerViewUtils.setFullSpanIfSupported(loadmoreViewHolder);

        return loadmoreViewHolder;
    }

    private RecyclerView.ViewHolder createThrobberViewHolder(ViewGroup parent, BasicViewMode viewMode) {
        BasicMVP_ThrobberViewHolder throbberViewHolder =
                new BasicMVP_ThrobberViewHolder(inflateLayout(R.layout.basic_list_item_throbber, parent));

        RecyclerViewUtils.setFullSpanIfSupported(throbberViewHolder);

        return throbberViewHolder;
    }

    
    protected View inflateLayout(int layoutId, ViewGroup parent) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layoutId, parent, false);
    }
}