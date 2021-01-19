package io.gitlab.aakumykov.sociocat.b_comments_list;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.b_comments_list.adapter_utils.CommentsList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.b_comments_list.adapter_utils.CommentsList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.b_comments_list.adapter_utils.CommentsList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.b_comments_list.interfaces.iCommentsList_ItemClickListener;
import io.gitlab.aakumykov.sociocat.b_comments_list.list_utils.CommentsList_ItemsComparator;

public class CommentsList_DataAdapter extends BasicMVPList_DataAdapter {

    public CommentsList_DataAdapter(BasicViewMode defaultViewMode, iBasicMVP_ItemClickListener itemClickListener) {
        super(defaultViewMode, itemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderCreator prepareViewHolderCreator() {
        return new CommentsList_ViewHolderCreator((iCommentsList_ItemClickListener) mItemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderBinder prepareViewHolderBinder() {
        return new CommentsList_ViewHolderBinder();
    }

    @Override
    protected BasicMVPList_ViewTypeDetector prepareViewTypeDetector() {
        return new CommentsList_ViewTypeDetector();
    }

    @Override
    public iItemsComparator getItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        return new CommentsList_ItemsComparator(sortingMode, sortingOrder);
    }
}
