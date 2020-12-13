package ru.aakumykov.me.sociocat.b_cards_list.list_utils;

import android.util.Log;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList_ItemsTextFilter extends BasicMVPList_ItemsTextFilter {

    private static final String TAG = CardsList_ItemsTextFilter.class.getSimpleName();

    public CardsList_ItemsTextFilter() {
        Log.d(TAG, "new CardsList_ItemsTextFilter()");
    }

    @Override
    protected boolean testDataItem(@NonNull BasicMVPList_DataItem dataItem, @NonNull String filterPattern) {

        Card card = (Card) dataItem.getPayload();

        String title = card.getTitle().toLowerCase();
        String patternReal = filterPattern.toLowerCase();

        boolean isMatch = title.contains(patternReal);

        return isMatch;
    }
}
