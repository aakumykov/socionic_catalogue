package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;


public class BasicMVP_ItemsFilter extends Filter {

    // Интерфейсы
    public interface CompleteCallback {
        void onListFiltered(List<BasicMVP_ListItem> filteredList);
    }


    // Свойства
    private final List<BasicMVP_ListItem> inputList;
    private final CompleteCallback completeCallback;


    //  Конструктор
    public BasicMVP_ItemsFilter(List<BasicMVP_ListItem> list4search, CompleteCallback callback) {
        this.completeCallback = callback;
        this.inputList = list4search;
    }


    // Собственные внешние методы
    public boolean itemIsFilterable(BasicMVP_ListItem basicListItem) {
        return basicListItem instanceof BasicMVP_DataItem;
    }

    public boolean itemIsPassedFilter(BasicMVP_ListItem basicListItem, CharSequence constraint) {
        String filterPattern = constraint.toString().toLowerCase();

        BasicMVP_DataItem basicDataItem = (BasicMVP_DataItem) basicListItem;
        String title = basicDataItem.getTitle();

        return title.toLowerCase().contains(filterPattern);
    }


    // Filer
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        List<BasicMVP_ListItem> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(constraint))
            filteredList.addAll(this.inputList);
        else {
            for (BasicMVP_ListItem basicListItem : this.inputList) {
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
