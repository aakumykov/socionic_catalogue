package ru.aakumykov.me.sociocat.tags_list.list_utils;

import android.util.Pair;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils.BasicMVP_ItemsComparator;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;

public class TagsList_ItemsComparator extends BasicMVP_ItemsComparator {

    private static final String TAG = TagsList_ItemsComparator.class.getSimpleName();


    @Override
    public int compare(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        if (mSortingMode instanceof eTagsList_SortingMode)
            return sortSelf(o1, o2, false);
        else
            return super.compare(o1, o2);
    }


    protected int compareSortableItems(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {

            eTagsList_SortingMode sortingMode = (eTagsList_SortingMode) mSortingMode;

            switch (sortingMode) {
                case BY_CARDS_COUNT:
                    return compareByCardsCount(o1, o2);
                case BY_SECOND_SYMBOL:
                    return compareBySecondSymbol(o1, o2);
                default:
                    return unknownSortingMode(TAG, mSortingMode);
            }
    }


    private int compareByCardsCount(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        Pair<Tag,Tag> tagsPair = getTagsPair(o1, o2);

        Integer cardsCount1 = tagsPair.first.getCardsCount();
        Integer cardsCount2 = tagsPair.second.getCardsCount();

        if (mSortingOrder.isDirect())
            return cardsCount1.compareTo(cardsCount2);
        else
            return cardsCount2.compareTo(cardsCount1);
    }

    private int compareBySecondSymbol(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        Pair<Tag,Tag> tagsPair = getTagsPair(o1, o2);

        String tagName1 = tagsPair.first.getName();
        String tagName2 = tagsPair.second.getName();

        String secondSymbol1 = String.valueOf(tagName1.charAt(1));
        String secondSymbol2 = String.valueOf(tagName2.charAt(1));

        if (mSortingOrder.isDirect())
            return secondSymbol1.compareTo(secondSymbol2);
        else
            return secondSymbol2.compareTo(secondSymbol1);
    }

    private Pair<Tag, Tag> getTagsPair(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        Tag tag1 = (Tag) ((BasicMVP_DataItem) o1).getPayload();
        Tag tag2 = (Tag) ((BasicMVP_DataItem) o2).getPayload();
        return new Pair<>(tag1, tag2);
    }
}












