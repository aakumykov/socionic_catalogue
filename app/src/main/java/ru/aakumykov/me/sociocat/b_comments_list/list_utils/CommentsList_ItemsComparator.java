package ru.aakumykov.me.sociocat.b_comments_list.list_utils;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsComparator;
import ru.aakumykov.me.sociocat.b_comments_list.enums.eCommentsList_SortingMode;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsList_ItemsComparator extends BasicMVPList_ItemsComparator {

    private static final String TAG = CommentsList_ItemsComparator.class.getSimpleName();


    public CommentsList_ItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        super(sortingMode, sortingOrder);
    }


    @Override
    public int compare(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        if (mSortingMode instanceof eCommentsList_SortingMode)
            return sortSelf(o1, o2, false);
        else
            return super.compare(o1, o2);
    }


    protected int sortSortableItems(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {

            eCommentsList_SortingMode sortingMode = (eCommentsList_SortingMode) mSortingMode;

            switch (sortingMode) {
                case BY_DATE:
                    return sortByCardsCount(sortingMode, o1, o2);
                default:
                    return unknownSortingMode(TAG, mSortingMode);
            }
    }


    private int sortByCardsCount(eCommentsList_SortingMode sortingMode, BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Comment comment1 = (Comment) ((BasicMVPList_DataItem) o1).getPayload();
        Comment comment2 = (Comment) ((BasicMVPList_DataItem) o2).getPayload();

        Long date1 = comment1.getDate();
        Long date2 = comment2.getDate();

        if (mSortingOrder.isDirect())
            return date1.compareTo(date2);
        else
            return date2.compareTo(date1);
    }
}












