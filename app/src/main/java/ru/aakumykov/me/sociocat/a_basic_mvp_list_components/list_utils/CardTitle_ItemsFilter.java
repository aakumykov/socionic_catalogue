package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardTitle_ItemsFilter extends BasicMVPList_ItemsFilter2 {

    private String mTextFilterPattern;

    @Override
    public void setFilterPattern(Object filterPattern) {
        mTextFilterPattern = (String) filterPattern;
    }

    @Override
    public Object getFilterPattern() {
        return mTextFilterPattern;
    }

    @Override
    protected boolean isMatch(BasicMVPList_DataItem dataItem) {
        Card card = (Card) dataItem.getPayload();

        String title = card.getTitle().toLowerCase();
        String patternReal = mTextFilterPattern.toLowerCase();

        boolean isMatch = title.contains(patternReal);
        return isMatch;
    }
}
