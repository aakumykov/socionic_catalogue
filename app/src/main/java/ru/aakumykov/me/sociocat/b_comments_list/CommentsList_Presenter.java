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
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_comments_list.enums.eCommentsList_SortingMode;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_ItemClickListener;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_View;
import ru.aakumykov.me.sociocat.b_comments_list.list_items.CommentsList_Item;
import ru.aakumykov.me.sociocat.b_comments_list.list_utils.CommentsList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.b_comments_list.stubs.CommentsList_ViewStub;
import ru.aakumykov.me.sociocat.b_comments_list.view_holders.CommentViewHolder;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsList_Presenter
        extends BasicMVPList_Presenter
        implements iCommentsList_ItemClickListener
{
    private static final String TAG = CommentsList_Presenter.class.getSimpleName();
    private final CommentsSingleton mCommentsSingleton = CommentsSingleton.getInstance();
//    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
//    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();
    private final boolean mInterruptFlag = false;

    public CommentsList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();

        mPageView.runDelayed(new Runnable() {
            @Override
            public void run() {
                makeStartDesision();
            }
        }, 500);
    }

    private void makeStartDesision() {
        Intent inputIntent = mPageView.getInputIntent();

        String userId = inputIntent.getStringExtra(Constants.USER_ID);

        if (null == userId)
            loadAllComments();
        else
            loadCommentsOfUser(userId);

    }

    private void loadAllComments() {

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

    private void loadCommentsOfUser(@NonNull String userId) {
        setRefreshingViewState();

        mCommentsSingleton.loadCommentsOfUser(userId, null, new iCommentsSingleton.ListCallbacks() {
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

    @Override
    protected void onRefreshRequested() {
        loadList();
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

        int position = commentViewHolder.getAdapterPosition();
        BasicMVPList_DataItem basicDataItem = (BasicMVPList_DataItem) mListView.getItem(position);
        Tag tag = (Tag) basicDataItem.getPayload();

        ((iCommentsList_View) mPageView).goShowCommentedCard(tag);
    }

    @Override
    public void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {
        onSelectItemClicked(basicDataViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder) {
        BasicMVPList_DataItem dataItem = mListView.getLastDataItem();

        if (null != dataItem) {
//            mListView.showLoadmoreItem(R.string.);
        }

    }

    @Override
    protected BasicMVPList_ItemsTextFilter getItemsTextFilter() {
        return new CommentsList_ItemsTextFilter();
    }

    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {

        Map<iSortingMode,eSortingOrder> sortingOrderMap = new HashMap<>();
        sortingOrderMap.put(eCommentsList_SortingMode.BY_DATE, eSortingOrder.REVERSE);
        sortingOrderMap.put(eBasicSortingMode.BY_NAME, eSortingOrder.DIRECT);

        if (sortingOrderMap.containsKey(sortingMode))
            return sortingOrderMap.get(sortingMode);

        return eSortingOrder.DIRECT;
    }

    /*@Override
    public void onInterruptRunningProcessClicked() {
        super.onInterruptRunningProcessClicked();
        mInterruptFlag = true;
    }*/


    // iTagsList_ItemClickListener
    /*@Override
    public void onEditTagClicked(CommentViewHolder commentViewHolder) {
        int adapterPosition = commentViewHolder.getAdapterPosition();

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.action_denied);
            return;
        }

        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) mListView.getItem(adapterPosition);
        Tag tag = (Tag) dataItem.getPayload();

        ((iCommentsList_View) mPageView).goEditTag(tag);
    }*/


    // Внутренние
    private void loadList() {

        setRefreshingViewState();

        /*mTagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {
            @Override
            public void onTagsListSuccess(List<Comment> commentsList) {
                setNeutralViewState();

                mListView.setList(
                        incapsulate2dataListItems(commentsList)
                );
            }

            @Override
            public void onTagsListFail(String errorMsg) {
                setErrorViewState(R.string.TAGS_LIST_error_loading_list, errorMsg);
            }
        });*/

//        mCommentsSingleton.load
    }

    private List<BasicMVPList_ListItem> incapsulate2dataListItems(List<Comment> commentsList) {
        List<BasicMVPList_ListItem> dataItemList = new ArrayList<>();
        for (Comment comment : commentsList)
            dataItemList.add(new CommentsList_Item(comment));
        return dataItemList;
    }


    /*public void onDeleteMenuItemClicked() {

        int count = mListView.getSelectedItemsCount();

        if (count > Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT) {
            String msg = TextUtils.getPluralString(
                    mPageView.getGlobalContext(),
                    R.plurals.TAGS_LIST_cannot_delete_more_tags_at_once,
                    Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT
            );
            mPageView.showToast(msg);
            return;
        }

        List<BasicMVPList_DataItem> selectedItems = mListView.getSelectedItems();
        StringBuilder messageBuilder = new StringBuilder();
        for (BasicMVPList_DataItem dataItem : selectedItems) {
            Tag tag = (Tag) dataItem.getPayload();
            messageBuilder.append(tag.getName());
            messageBuilder.append("\n");
        }

        String title = TextUtils.getPluralString(
                mPageView.getGlobalContext(),
                R.plurals.TAGS_LIST_deleting_dialog_title,
                count
        );

        SimpleYesNoDialog.show(
                mPageView.getLocalContext(),
                title,
                messageBuilder.toString(),
                new SimpleYesNoDialog.AbstractCallbacks() {
                    @Override
                    public void onYes() {
                        super.onYes();
                        onTagsDeletionConfirmed();
                    }
                }
        );
    }*/

    /*private void onTagsDeletionConfirmed() {

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showSnackbar(R.string.TAGS_LIST_you_cannot_delete_tags, R.string.SNACKBAR_got_it, 10000);
            mListView.clearSelection();
            setNeutralViewState();
            return;
        }

        deleteTagsFromList(mListView.getSelectedItems());
    }*/

    /*private void deleteTagsFromList(List<BasicMVPList_DataItem> itemsList) {

        if (hasInterruptFlag()) {
            mPageView.showToast(R.string.TAGS_LIST_deletion_process_interrupted);
            mListView.clearSelection();
            setNeutralViewState();
            clearInterruptFlag();
            return;
        }

        if (0 == itemsList.size()) {
            mPageView.showStyledToast(R.string.TAGS_LIST_selected_tags_are_processed);
            setNeutralViewState();
            return;
        }

        BasicMVPList_DataItem dataItem = itemsList.get(0);
        itemsList.remove(0);
        Tag tag = (Tag) dataItem.getPayload();

        String msg = TextUtils.getText(mPageView.getGlobalContext(), R.string.TAGS_LIST_deleting_tag, tag.getName());
        setViewState(new CancelableProgressViewState(msg));

        mComplexSingleton.deleteTag(tag, new ComplexSingleton.iComplexSingleton_TagDeletionCallbacks() {
            @Override
            public void onTagDeleteSuccess(@NonNull Tag tag) {
                mListView.removeItem(dataItem);
                deleteTagsFromList(itemsList);
            }

            @Override
            public void onTagDeleteError(@NonNull String errorMsg) {
                String msg = mPageView.getText(R.string.TAGS_LIST_error_deleting_tag, tag.getName());
                setViewState(new ErrorViewState(msg, errorMsg));
                Log.e(TAG, errorMsg);
            }
        });
    }*/


    // TODO: нужно ли?
    public void onTagEdited(Comment oldComment, Comment newComment) {
        if (null != oldComment && null != newComment)
        {
//            int position = ((TagsList_DataAdapter) mListView).updateItemInList(oldTag, newTag);

            CommentsList_Item newTagListItem = new CommentsList_Item(newComment);

            int position = mListView.findAndUpdateItem(newTagListItem, new iBasicList.iFindItemComparisionCallback() {
                @Override
                public boolean onCompareWithListItemPayload(Object itemPayload) {
                    Tag tagFromList = (Tag) itemPayload;
                    return tagFromList.getKey().equals(oldComment.getKey());
                }
            });

            mPageView.scroll2position(position);
            mListView.highlightItem(position);
        }
    }

    /*public boolean canDeleteTag() {
        return mUsersSingleton.currentUserIsAdmin();
    }*/
}









