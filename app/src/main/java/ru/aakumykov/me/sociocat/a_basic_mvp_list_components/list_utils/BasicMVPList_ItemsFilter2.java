package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils;

import java.util.function.Predicate;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class BasicMVPList_ItemsFilter2 implements Predicate<BasicMVPList_ListItem> {

    private String mFilterPattern;

    public void setFilterPattern(String filterPattern) {
        mFilterPattern = filterPattern;
    }

    @Override
    public boolean test(BasicMVPList_ListItem listItem) {
        if (listItem instanceof BasicMVPList_DataItem) {
            BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;
            Card card = (Card) dataItem.getPayload();
//            return card.getTitle().toLowerCase().equals(mFilterPattern.toLowerCase());

            String title = card.getTitle().toLowerCase();
            String patternReal = mFilterPattern.toLowerCase();

            boolean isMatch = title.contains(patternReal);

            return isMatch;
        }
        return false;
    }
}
