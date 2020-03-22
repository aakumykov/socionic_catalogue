package ru.aakumykov.me.sociocat.template_of_list.list_items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.BasicViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.DataItem_ViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.Unknown_ViewHolder;

public class ListItemsFactory {

    @NonNull
    public static BasicViewHolder createViewHolder(int viewType, ViewGroup parent) {

        if (viewType == iItemsList.DATA_ITEM_TYPE) {
            View itemView = createItemView(parent, R.layout.template_of_list_data_item);
            return new DataItem_ViewHolder(itemView);
        }
        else if (viewType == iItemsList.LOADMORE_ITEM_TYPE) {
            View itemView = createItemView(parent, R.layout.template_of_list_loadmore_item);
            return setFullSpanIfSupported(new LoadMore_ViewHolder(itemView));
        }
        else if (viewType == iItemsList.THROBBER_ITEM_TYPE) {
            View itemView = createItemView(parent, R.layout.template_of_list_throbber_item);
            return setFullSpanIfSupported(new Throbber_ViewHolder(itemView));
        }
        else {
            View itemView = createItemView(parent, R.layout.template_of_list_unknown_item);
            return new Unknown_ViewHolder(itemView);
        }
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
