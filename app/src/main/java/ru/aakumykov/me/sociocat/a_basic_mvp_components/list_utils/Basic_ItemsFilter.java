package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;


public class Basic_ItemsFilter extends Filter {

    // Интерфейсы
    public interface CompleteCallback {
        void onListFiltered(List<Basic_ListItem> filteredList);
    }


    // Свойства
    private final List<Basic_ListItem> inputList;
    private final CompleteCallback completeCallback;


    //  Конструктор
    public Basic_ItemsFilter(List<Basic_ListItem> list4search, CompleteCallback callback) {
        this.completeCallback = callback;
        this.inputList = list4search;
    }


    // Собственные внешние методы
    public boolean itemIsFilterable(Basic_ListItem basicListItem) {
        return basicListItem instanceof Basic_DataItem;
    }

    public boolean itemIsPassedFilter(Basic_ListItem basicListItem, CharSequence constraint) {
        String filterPattern = constraint.toString().toLowerCase();

        Basic_DataItem basicDataItem = (Basic_DataItem) basicListItem;
        String title = basicDataItem.getTitle();

        return title.toLowerCase().contains(filterPattern);
    }


    // Filer
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        List<Basic_ListItem> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(constraint))
            filteredList.addAll(this.inputList);
        else {
            for (Basic_ListItem basicListItem : this.inputList) {
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
