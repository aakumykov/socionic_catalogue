package ru.aakumykov.me.sociocat.b_cards_list.list_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList_ItemsTextFilter extends BasicMVPList_ItemsTextFilter {

    @Override
    protected boolean testDataItem(@NonNull BasicMVPList_DataItem dataItem, @NonNull String filterPattern) {

        Card card = (Card) dataItem.getPayload();

        String title = card.getTitle().toLowerCase();
        String patternReal = filterPattern.toLowerCase();

        return title.contains(patternReal);
    }
}
