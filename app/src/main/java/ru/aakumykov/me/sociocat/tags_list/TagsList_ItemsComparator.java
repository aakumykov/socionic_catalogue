package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils.BasicMVP_ItemsComparator;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList_ItemsComparator extends BasicMVP_ItemsComparator {

    private static final String TAG = TagsList_ItemsComparator.class.getSimpleName();

    @Override
    public int compare(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        if (mSortingMode instanceof eTagsList_SortingMode)
            return sortByOwn(o1, o2);
        else
            return super.compare(o1, o2);
    }

    private int sortByOwn(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        if (hasPinnedItems(o1, o2))
            return sortPinnedItems(o1, o2);
        else
            return sortMiddleItems(o1, o2);
    }

    protected int sortMiddleItems(BasicMVP_ListItem o1, BasicMVP_ListItem o2) {

        if (mSortingMode instanceof eTagsList_SortingMode) {
            eTagsList_SortingMode sortingMode = (eTagsList_SortingMode) mSortingMode;

            switch (sortingMode) {
                case CARDS_COUNT:
                    return sortByCardsCount(sortingMode, o1, o2);
                default:
                    return unknownSortingMode(TAG, mSortingMode);
            }
        }
        else {
            return super.sortMiddleItems(o1, o2);
        }
    }

    private int sortByCardsCount(eTagsList_SortingMode sortingMode, BasicMVP_ListItem o1, BasicMVP_ListItem o2) {
        Tag tag1 = (Tag) ((BasicMVP_DataItem) o1).getPayload();
        Tag tag2 = (Tag) ((BasicMVP_DataItem) o2).getPayload();

        Integer cardsCount1 = tag1.getCardsCount();
        Integer cardsCount2 = tag2.getCardsCount();

        if (mSortingOrder.isDirect())
            return cardsCount1.compareTo(cardsCount2);
        else
            return cardsCount2.compareTo(cardsCount1);
    }
}












