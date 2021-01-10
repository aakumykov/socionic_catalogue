package ru.aakumykov.me.sociocat.b_comments_list;

import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.CancelableProgressViewState;
import ru.aakumykov.me.sociocat.b_comments_list.enums.eCommentsList_SortingMode;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_ItemClickListener;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_View;
import ru.aakumykov.me.sociocat.b_comments_list.list_items.Comment_ListItem;
import ru.aakumykov.me.sociocat.b_comments_list.list_utils.CommentsList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.b_comments_list.stubs.CommentsList_ViewStub;
import ru.aakumykov.me.sociocat.b_comments_list.view_holders.CommentViewHolder;
import ru.aakumykov.me.sociocat.b_comments_list.view_states.CommentsOfUser_ViewState;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CommentsComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.SimpleYesNoDialog;

public class CommentsList_Presenter
        extends BasicMVPList_Presenter
        implements iCommentsList_ItemClickListener
{
    private static final String TAG = CommentsList_Presenter.class.getSimpleName();
    private final CommentsSingleton mCommentsSingleton = CommentsSingleton.getInstance();
//    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
//    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();
    private User mCommentsOwnerUser;
    private iCommentsComplexSingleton mCommentsComplexSingleton = CommentsComplexSingleton.getInstance();


    public CommentsList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();

        mPageView.runDelayed(this::makeStartDecision, 500);
    }

    @Override
    protected void onRefreshRequested() {
        makeStartDecision();
    }

    @Override
    public void unbindViews() {
        mPageView = new CommentsList_ViewStub();
    }

    @Override
    public void onItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {

        CommentViewHolder commentViewHolder = (CommentViewHolder) basicDataViewHolder;

        if (mListView.isSelectionMode()) {
            onSelectItemClicked(commentViewHolder);
            return;
        }

        Comment comment = getCommentForViewHolder(basicDataViewHolder);

        ((iCommentsList_View) mPageView).goShowCommentUnderCard(comment);
    }

    @Override
    public void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {
        onSelectItemClicked(basicDataViewHolder);
    }

    @Override
    public void onCardTitleClicked(@NonNull CommentViewHolder commentViewHolder) {
        Comment comment = getCommentForViewHolder(commentViewHolder);
        ((CommentsList_View) mPageView).goShowCommentedCard(comment);
    }

    @Override
    public void onCardAuthorClicked(@NonNull CommentViewHolder commentViewHolder) {
        Comment comment = getCommentForViewHolder(commentViewHolder);
        ((CommentsList_View) mPageView).goShowUserProfile(comment.getUserId());
    }

    @Override
    public void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder) {
        BasicMVPList_DataItem lastDataItem = mListView.getLastDataItem();

        if (null == lastDataItem) {
            mListView.hideLoadmoreItem();
            mListView.showLoadmoreItem(R.string.no_more);
            return;
        }

        Comment lastComment = (Comment) lastDataItem.getPayload();

        mListView.hideLoadmoreItem();
        mListView.showThrobberItem();

        if (null == mCommentsOwnerUser)
            loadMoreComments(lastComment);
        else
            loadMoreCommentsOfUser(lastComment);
    }

    @Override
    protected BasicMVPList_ItemsTextFilter getItemsTextFilter() {
        return new CommentsList_ItemsTextFilter();
    }

    @Override
    public void onSelectItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {
        if (UsersSingleton.getInstance().currentUserIsAdmin())
            super.onSelectItemClicked(basicDataViewHolder);
    }

    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {

        Map<iSortingMode,eSortingOrder> sortingOrderMap = new HashMap<>();
        sortingOrderMap.put(eBasicSortingMode.BY_DATE, eSortingOrder.REVERSE);
        sortingOrderMap.put(eCommentsList_SortingMode.BY_AUTHOR, eSortingOrder.DIRECT);
        sortingOrderMap.put(eCommentsList_SortingMode.BY_CARD, eSortingOrder.DIRECT);

        if (sortingOrderMap.containsKey(sortingMode))
            return sortingOrderMap.get(sortingMode);

        return eSortingOrder.DIRECT;
    }


    // Внутренние
    private void makeStartDecision() {
        Intent inputIntent = mPageView.getInputIntent();

        mCommentsOwnerUser = inputIntent.getParcelableExtra(Constants.USER);

        if (null == mCommentsOwnerUser)
            loadComments();
        else
            loadCommentsOfUser(mCommentsOwnerUser);

    }


    private void loadComments() {

        setRefreshingViewState();

        mCommentsSingleton.loadComments(null, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                setNeutralViewState();
                mListView.setList(incapsulate2dataListItems(list));
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                setErrorViewState(R.string.COMMENTS_LIST_error_loading_comments, errorMessage);
            }
        });
    }

    private void loadCommentsOfUser(@NonNull User user) {
        setRefreshingViewState();
        mCommentsSingleton.loadCommentsOfUser(user.getKey(), null, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                setCommentsOfUserViewState();
                mListView.setList(incapsulate2dataListItems(list));
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                setErrorViewState(R.string.COMMENTS_LIST_error_loading_comments, errorMessage);
            }
        });
    }


    private void loadMoreComments(@NonNull Comment lastComment) {

        mCommentsSingleton.loadComments(lastComment, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                setNeutralViewState();
                mListView.appendList(incapsulate2dataListItems(list));
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                setErrorViewState(R.string.COMMENTS_LIST_error_loading_comments, errorMessage);
            }
        });
    }

    private void loadMoreCommentsOfUser(@NonNull Comment lastComment) {

        mCommentsSingleton.loadCommentsOfUser(mCommentsOwnerUser.getKey(),
                lastComment, new iCommentsSingleton.ListCallbacks() {
                    @Override
                    public void onCommentsLoadSuccess(List<Comment> list) {
                        setCommentsOfUserViewState();
                        mListView.appendList(incapsulate2dataListItems(list));
                    }

                    @Override
                    public void onCommentsLoadError(String errorMessage) {
                        setErrorViewState(R.string.COMMENTS_LIST_error_loading_comments, errorMessage);
                    }
                });
    }


    private void setCommentsOfUserViewState() {
        setViewState(new CommentsOfUser_ViewState(mCommentsOwnerUser.getName()));
    }

    private List<BasicMVPList_ListItem> incapsulate2dataListItems(List<Comment> commentsList) {
        List<BasicMVPList_ListItem> dataItemList = new ArrayList<>();
        for (Comment comment : commentsList)
            dataItemList.add(new Comment_ListItem(comment));
        return dataItemList;
    }

    private Comment getCommentForViewHolder(@NonNull BasicMVPList_DataViewHolder commentViewHolder) {
        int position = commentViewHolder.getAdapterPosition();
        return (Comment) ((Comment_ListItem) mListView.getItem(position)).getPayload();
    }

    public boolean canDeleteComments() {
        return UsersSingleton.getInstance().currentUserIsAdmin();
    }

    public boolean commentsCountIsDeletable(int selectedItemsCount) {
        return selectedItemsCount <= Constants.MAX_COMMENTS_AT_ONCE_DELETE_COUNT;
    }

    public void onDeleteCommentsClicked() {
        int count = mListView.getSelectedItemsCount();

        String commentPluralWord = TextUtils.getPluralString(mPageView.getLocalContext(),
                R.plurals.comment, count);

        String message = count + " " + commentPluralWord;

        SimpleYesNoDialog.show(
                mPageView.getLocalContext(),
                R.string.COMMENTS_LIST_delete_selected_comments,
                message,
                new SimpleYesNoDialog.AbstractCallbacks() {
                    @Override
                    public void onYes() {
                        onDeleteCommentsConfirmed();
                    }
                }
        );
    }

    private void onDeleteCommentsConfirmed() {
        List<BasicMVPList_DataItem> list = mListView.getSelectedItems();
        deleteCommentFromList(list);
    }

    private void deleteCommentFromList(List<BasicMVPList_DataItem> inputList) {
        if (hasInterruptFlag()) {
            mPageView.showToast(R.string.deletion_process_is_interrupted);
            setNeutralViewState();
            return;
        }

        if (0 == inputList.size()) {
            mPageView.showToast(R.string.deletion_process_is_finished);
            setNeutralViewState();
            return;
        }

        BasicMVPList_DataItem dataItem = inputList.get(0);
        inputList.remove(0);

        Comment comment = (Comment) dataItem.getPayload();

        String commentText = MyUtils.cutToLength(comment.getText(), 20);
        String msg = mPageView.getText(R.string.COMMENTS_LIST_deleting_comment, commentText);
        setViewState(new CancelableProgressViewState(msg));

        mCommentsComplexSingleton.deleteComment(comment, new iCommentsComplexSingleton.CommentDeletionCallbacks() {
            @Override
            public void onCommentDeleteSuccess(@NonNull Comment comment) {
                String toastMsg = mPageView.getText(R.string.COMMENTS_LIST_comment_has_been_deleted, commentText);
                mPageView.showToast(toastMsg);

                mListView.removeItem(dataItem);

                deleteCommentFromList(inputList);
            }

            @Override
            public void onCommentDeleteError(@NonNull String errorMsg) {
                String toastMsg = mPageView.getText(R.string.COMMENTS_LIST_error_deleting_comment, commentText);
                mPageView.showToast(toastMsg);

                deleteCommentFromList(inputList);
            }
        });
    }

}









