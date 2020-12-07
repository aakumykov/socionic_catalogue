package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils;

import java.util.function.Predicate;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public abstract class BasicMVPList_ItemsFilter2 implements Predicate<BasicMVPList_ListItem> {

    public abstract void setFilterPattern(Object filterPattern);
    public abstract Object getFilterPattern();
    protected abstract boolean isMatch(BasicMVPList_DataItem dataItem);

    @Override
    public boolean test(BasicMVPList_ListItem listItem) {
        if (listItem instanceof BasicMVPList_DataItem) {
            BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;
            return isMatch(dataItem);
        }
        return false;
    }
}
