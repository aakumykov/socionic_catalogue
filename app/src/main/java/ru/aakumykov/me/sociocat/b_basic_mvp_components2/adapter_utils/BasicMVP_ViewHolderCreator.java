package ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.exceptions.UnknownViewModeException;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.RecyclerViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_LoadmoreViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ThrobberViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.FeedViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.GridViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;

public abstract class BasicMVP_ViewHolderCreator {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVP_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, BasicViewMode viewMode) {

        switch (viewType) {
            case BasicMVP_ItemTypes.DATA_ITEM:
                return createDataViewHolder(parent, viewMode);

            case BasicMVP_ItemTypes.LOADMORE_ITEM:
                return createLoadmoreViewHolder(parent, viewMode);

            case BasicMVP_ItemTypes.THROBBER_ITEM:
                return createThrobberViewHolder(parent, viewMode);

            default:
                throw new RuntimeException("Неизвестный viewType: "+viewType);
        }
    }

    public abstract BasicMVP_DataViewHolder createViewHolder4listMode(ViewGroup parent);

    public abstract BasicMVP_DataViewHolder createViewHolder4feedMode(ViewGroup parent);

    public abstract BasicMVP_DataViewHolder createViewHolder4gridMode(ViewGroup parent);


    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        BasicMVP_DataViewHolder dataViewHolder;

        if (viewMode instanceof ListViewMode) {
            dataViewHolder = createViewHolder4listMode(parent);
        }
        else if (viewMode instanceof FeedViewMode) {
            dataViewHolder = createViewHolder4feedMode(parent);
        }
        else if (viewMode instanceof GridViewMode) {
            dataViewHolder = createViewHolder4gridMode(parent);
        }
        else {
            throw new UnknownViewModeException(viewMode);
        }

        dataViewHolder.setItemClickListener(mItemClickListener);

        return dataViewHolder;
    }

    private BasicMVP_LoadmoreViewHolder createLoadmoreViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        BasicMVP_LoadmoreViewHolder loadmoreViewHolder =
                new BasicMVP_LoadmoreViewHolder(inflateItemView(parent, R.layout.basic_list_item_loadmore));

        loadmoreViewHolder.setItemClickListener(mItemClickListener);

        RecyclerViewUtils.setFullSpanIfSupported(loadmoreViewHolder);

        return loadmoreViewHolder;
    }

    private RecyclerView.ViewHolder createThrobberViewHolder(ViewGroup parent, BasicViewMode viewMode) {
        BasicMVP_ThrobberViewHolder throbberViewHolder =
                new BasicMVP_ThrobberViewHolder(inflateItemView(parent, R.layout.basic_list_item_throbber));

        RecyclerViewUtils.setFullSpanIfSupported(throbberViewHolder);

        return throbberViewHolder;
    }


    protected View inflateItemView(ViewGroup parent, int layoutResourceId) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layoutResourceId, parent, false);
    }

}