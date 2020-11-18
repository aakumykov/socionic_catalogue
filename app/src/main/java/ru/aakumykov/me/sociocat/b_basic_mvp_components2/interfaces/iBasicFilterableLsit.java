package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import android.widget.Filterable;

public interface iBasicFilterableLsit extends Filterable {

    void prepareFilter();
    void removeFilter();

    String getFilterText();

    void filterItems(String pattern);
    boolean isFiltered();
}
