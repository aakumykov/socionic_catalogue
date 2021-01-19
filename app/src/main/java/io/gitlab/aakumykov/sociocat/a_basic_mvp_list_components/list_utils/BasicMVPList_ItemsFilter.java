package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_utils;


import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;

public class BasicMVPList_ItemsFilter extends Filter {

    // Интерфейсы
    public interface CompleteCallback {
        void onListFiltered(List<BasicMVPList_ListItem> filteredList);
    }


    // Свойства
    private final List<BasicMVPList_ListItem> inputList;
    private final CompleteCallback completeCallback;


    //  Конструктор
    public BasicMVPList_ItemsFilter(List<BasicMVPList_ListItem> list4search, CompleteCallback callback) {
        this.completeCallback = callback;
        this.inputList = list4search;
    }


    // Собственные внешние методы
    public boolean itemIsFilterable(BasicMVPList_ListItem basicListItem) {
        return basicListItem instanceof BasicMVPList_DataItem;
    }

    public boolean itemIsPassedFilter(BasicMVPList_ListItem basicListItem, CharSequence constraint) {
        String filterPattern = constraint.toString().toLowerCase();

        BasicMVPList_DataItem basicDataItem = (BasicMVPList_DataItem) basicListItem;
        String title = basicDataItem.getTitle();

        return title.toLowerCase().contains(filterPattern);
    }


    // Filer
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        List<BasicMVPList_ListItem> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(constraint))
            filteredList.addAll(this.inputList);
        else {
            for (BasicMVPList_ListItem basicListItem : this.inputList) {
                if (itemIsFilterable(basicListItem))
                    if (itemIsPassedFilter(basicListItem, constraint))
                        filteredList.add(basicListItem);
            }
        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredList;
        return filterResults;
    }


    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        completeCallback.onListFiltered((List) results.values);
    }

}
