package io.gitlab.aakumykov.sociocat.c_tags_list.list_utils;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsComparator;
import io.gitlab.aakumykov.sociocat.c_tags_list.enums.eTagsList_SortingMode;
import io.gitlab.aakumykov.sociocat.models.Tag;

public class TagsList_ItemsComparator extends BasicMVPList_ItemsComparator {

    private static final String TAG = TagsList_ItemsComparator.class.getSimpleName();


    public TagsList_ItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        super(sortingMode, sortingOrder);
    }


    @Override
    public int compare(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        if (mSortingMode instanceof eTagsList_SortingMode)
            return sortSelf(o1, o2, false);
        else
            return super.compare(o1, o2);
    }


    protected int sortSortableItems(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {

            eTagsList_SortingMode sortingMode = (eTagsList_SortingMode) mSortingMode;

            switch (sortingMode) {
                case BY_CARDS_COUNT:
                    return sortByCardsCount(sortingMode, o1, o2);
                default:
                    return unknownSortingMode(TAG, mSortingMode);
            }
    }


    private int sortByCardsCount(eTagsList_SortingMode sortingMode, BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Tag tag1 = (Tag) ((BasicMVPList_DataItem) o1).getPayload();
        Tag tag2 = (Tag) ((BasicMVPList_DataItem) o2).getPayload();

        Integer cardsCount1 = tag1.getCardsCount();
        Integer cardsCount2 = tag2.getCardsCount();

        if (mSortingOrder.isDirect())
            return cardsCount1.compareTo(cardsCount2);
        else
            return cardsCount2.compareTo(cardsCount1);
    }
}












