package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.exceptions.UnknownViewModeException;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.RecyclerViewUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_LoadmoreViewHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ThrobberViewHolder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.FeedViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.GridViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;

public abstract class BasicMVPList_ViewHolderCreator {

    protected iBasicMVP_ItemClickListener mItemClickListener;

    public BasicMVPList_ViewHolderCreator(iBasicMVP_ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, BasicViewMode viewMode) {

        switch (viewType) {
            case BasicMVPList_ItemTypes.DATA_ITEM:
                return createDataViewHolder(parent, viewMode);

            case BasicMVPList_ItemTypes.LOADMORE_ITEM:
                return createLoadmoreViewHolder(parent, viewMode);

            case BasicMVPList_ItemTypes.THROBBER_ITEM:
                return createThrobberViewHolder(parent, viewMode);

            default:
                throw new RuntimeException("Неизвестный viewType: "+viewType);
        }
    }

    public abstract BasicMVPList_DataViewHolder createViewHolder4listMode(ViewGroup parent);

    public abstract BasicMVPList_DataViewHolder createViewHolder4feedMode(ViewGroup parent);

    public abstract BasicMVPList_DataViewHolder createViewHolder4gridMode(ViewGroup parent);


    private RecyclerView.ViewHolder createDataViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        BasicMVPList_DataViewHolder dataViewHolder;

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

    private BasicMVPList_LoadmoreViewHolder createLoadmoreViewHolder(ViewGroup parent, BasicViewMode viewMode) {

        BasicMVPList_LoadmoreViewHolder loadmoreViewHolder =
                new BasicMVPList_LoadmoreViewHolder(inflateItemView(parent, R.layout.basic_list_item_loadmore));

        loadmoreViewHolder.setItemClickListener(mItemClickListener);

        RecyclerViewUtils.setFullSpanIfSupported(loadmoreViewHolder);

        return loadmoreViewHolder;
    }

    private RecyclerView.ViewHolder createThrobberViewHolder(ViewGroup parent, BasicViewMode viewMode) {
        BasicMVPList_ThrobberViewHolder throbberViewHolder =
                new BasicMVPList_ThrobberViewHolder(inflateItemView(parent, R.layout.basic_list_item_throbber));

        RecyclerViewUtils.setFullSpanIfSupported(throbberViewHolder);

        return throbberViewHolder;
    }


    protected View inflateItemView(ViewGroup parent, int layoutResourceId) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(layoutResourceId, parent, false);
    }

}