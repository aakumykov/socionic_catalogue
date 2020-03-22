package ru.aakumykov.me.sociocat.cards_list.list_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.view_holders.BasicViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.DataItem_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.Unknown_ViewHolder;

public class ListItemsFactory {

    @NonNull
    public static BasicViewHolder createViewHolder(int viewType, ViewGroup parent, iCardsList.ViewMode viewMode) {
        switch (viewType) {
            case iCardsList.DATA_ITEM_TYPE:
                return dataItemViewHolder(parent, viewMode);

            case iCardsList.LOADMORE_ITEM_TYPE:
                return loadmoreViewHolder(parent, viewMode);

            case iCardsList.THROBBER_ITEM_TYPE:
                return throbberItemViewHolder(parent, viewMode);

            default:
                return unknownItemViewHolder(parent, viewMode);
        }
    }

    private static BasicViewHolder unknownItemViewHolder(ViewGroup parent, iCardsList.ViewMode viewMode) {
        View itemView = createItemView(parent, R.layout.cards_list_item_unknown);
        return new Unknown_ViewHolder(itemView);
    }

    private static BasicViewHolder throbberItemViewHolder(ViewGroup parent, iCardsList.ViewMode viewMode) {
        View itemView = createItemView(parent, R.layout.cards_list_item_throbber);
        return setFullSpanIfSupported(new Throbber_ViewHolder(itemView));
    }

    private static BasicViewHolder loadmoreViewHolder(ViewGroup parent, iCardsList.ViewMode viewMode) {
        View itemView = createItemView(parent, R.layout.cards_list_item_loadmore);
        return setFullSpanIfSupported(new LoadMore_ViewHolder(itemView));
    }

    private static BasicViewHolder dataItemViewHolder(ViewGroup parent, iCardsList.ViewMode viewMode) {
        int layoutResourceId = (iCardsList.ViewMode.LIST.equals(viewMode)) ?
                R.layout.cards_list_item_data_list_mode :
                R.layout.cards_list_item_data_grid_mode;

        View itemView = createItemView(parent, layoutResourceId);

        return new DataItem_ViewHolder(itemView);
    }

    private static View createItemView(ViewGroup parent, int layoutResourceId) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return layoutInflater.inflate(layoutResourceId, parent, false);
    }

    private static BasicViewHolder setFullSpanIfSupported(BasicViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
        }
        return viewHolder;
    }
}
