package ru.aakumykov.me.sociocat.cards_list2.list_utils;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsComparator;
import ru.aakumykov.me.sociocat.cards_list2.enums.eCardsList2_SortingMode;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_ItemsComparator extends BasicMVPList_ItemsComparator {

    private static final String TAG = CardsList2_ItemsComparator.class.getSimpleName();

    @Override
    public int compare(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        if (mSortingMode instanceof eCardsList2_SortingMode)
            return sortSelf(o1, o2, false);
        else
            return super.compare(o1, o2);
    }

    @Override
    protected int sortSortableItems(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {

        eCardsList2_SortingMode sortingMode = (eCardsList2_SortingMode) mSortingMode;

        switch (sortingMode) {
            case BY_AUTHOR:
                return sortByAuthor(sortingMode, o1, o2);

            case BY_RATING:
                return sortByRating(sortingMode, o1, o2);

            case BY_COMMENTS:
                return sortByComments(sortingMode, o1, o2);

            default:
                return unknownSortingMode(TAG, mSortingMode);
        }
    }


    private int sortByAuthor(eCardsList2_SortingMode sortingMode, BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Card card1 = (Card) ((BasicMVPList_DataItem) o1).getPayload();
        Card card2 = (Card) ((BasicMVPList_DataItem) o2).getPayload();

        String author1 = card1.getUserName();
        String author2 = card2.getUserName();

        if (mSortingOrder.isDirect())
            return author1.compareTo(author2);
        else
            return author2.compareTo(author1);
    }


    private int sortByRating(eCardsList2_SortingMode sortingMode, BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Card card1 = (Card) ((BasicMVPList_DataItem) o1).getPayload();
        Card card2 = (Card) ((BasicMVPList_DataItem) o2).getPayload();

        Integer rating1 = card1.getRating();
        Integer rating2 = card2.getRating();

        if (mSortingOrder.isDirect())
            return rating1.compareTo(rating2);
        else
            return rating2.compareTo(rating1);
    }


    private int sortByComments(eCardsList2_SortingMode sortingMode, BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Card card1 = (Card) ((BasicMVPList_DataItem) o1).getPayload();
        Card card2 = (Card) ((BasicMVPList_DataItem) o2).getPayload();

        Integer commentsCount1 = card1.getCommentsKeys().size();
        Integer commentsCount2 = card2.getCommentsKeys().size();

        if (mSortingOrder.isDirect())
            return commentsCount1.compareTo(commentsCount2);
        else
            return commentsCount2.compareTo(commentsCount1);
    }


}
