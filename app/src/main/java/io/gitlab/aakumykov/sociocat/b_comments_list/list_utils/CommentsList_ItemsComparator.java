package io.gitlab.aakumykov.sociocat.b_comments_list.list_utils;

import android.util.Pair;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsComparator;
import io.gitlab.aakumykov.sociocat.b_comments_list.enums.eCommentsList_SortingMode;
import io.gitlab.aakumykov.sociocat.models.Comment;

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

            iSortingMode sortingMode = (eCommentsList_SortingMode) mSortingMode;

            if (sortingMode instanceof eBasicSortingMode) {
                switch ((eBasicSortingMode) sortingMode) {
                    case BY_DATE:
                        return sortByDate(o1, o2);
                    default:
                        return unknownSortingMode(TAG, mSortingMode);
                }
            }
            else if (sortingMode instanceof eCommentsList_SortingMode) {
                switch ((eCommentsList_SortingMode) sortingMode) {
                    case BY_AUTHOR:
                        return sortByAuthor(o1, o2);
                    case BY_CARD:
                        return sortByCard(o1, o2);
                    default:
                        return unknownSortingMode(TAG, mSortingMode);
                }
            }
            else
                return unknownSortingMode(TAG, mSortingMode);
    }


    private int sortByAuthor(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Pair<Comment, Comment> commentsPair = getCommentsPair(o1, o2);

        String userName1 = commentsPair.first.getUserName();
        String userName2 = commentsPair.second.getUserName();

        if (null == userName1)
            userName1 = "";

        if (null == userName2)
            userName2 = "";

        if (mSortingOrder.isDirect())
            return userName1.compareTo(userName2);
        else
            return userName2.compareTo(userName1);
    }

    private int sortByCard(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Pair<Comment, Comment> commentsPair = getCommentsPair(o1, o2);

        String card1Title = commentsPair.first.getCardTitle();
        String card2Title = commentsPair.second.getCardTitle();

        if (null == card1Title)
            card1Title = "";

        if (null == card2Title)
            card2Title = "";

        if (mSortingOrder.isDirect())
            return card1Title.compareTo(card2Title);
        else
            return card2Title.compareTo(card1Title);
    }

    private Pair<Comment, Comment> getCommentsPair(BasicMVPList_ListItem o1, BasicMVPList_ListItem o2) {
        Comment comment1 = (Comment) ((BasicMVPList_DataItem) o1).getPayload();
        Comment comment2 = (Comment) ((BasicMVPList_DataItem) o2).getPayload();
        return new Pair<Comment,Comment>(comment1, comment2);
    }
}












