package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils;

import java.util.function.Predicate;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class BasicMVPList_ItemsTextFilter implements Predicate<BasicMVPList_ListItem> {

    private String mFilterPattern;

    @Override
    public boolean test(BasicMVPList_ListItem listItem) {

        if (listItem instanceof BasicMVPList_DataItem)
        {
            if ("".equals(mFilterPattern))
                return true;

            BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;

            Card card = (Card) dataItem.getPayload();

            String title = card.getTitle().toLowerCase();
            String patternReal = mFilterPattern.toLowerCase();

            return title.contains(patternReal);
        }
        return false;
    }

    public void setFilterPattern(String filterPattern) {
        mFilterPattern = filterPattern;
    }

    public String getFilterText() {
        return mFilterPattern;
    }

    public boolean alreadyFilteredWith(String textPattern) {
        return null != mFilterPattern && mFilterPattern.equals(textPattern);
    }
}
