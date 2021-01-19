package io.gitlab.aakumykov.sociocat.b_comments_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_View;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iDataAdapterPreparationCallback;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iPresenterPreparationCallback;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.builders.SortingMenuItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.SomeItemsSelectedViewState;
import io.gitlab.aakumykov.sociocat.b_comments_list.enums.eCommentsList_SortingMode;
import io.gitlab.aakumykov.sociocat.b_comments_list.interfaces.iCommentsList_View;
import io.gitlab.aakumykov.sociocat.b_comments_list.view_states.CommentsOfUser_ViewState;
import io.gitlab.aakumykov.sociocat.card_show.CardShow_View;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.models.Comment;
import io.gitlab.aakumykov.sociocat.user_show.UserShow_View;

public class CommentsList_View extends BasicMVPList_View implements iCommentsList_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultPageTitle();
        activateUpButton();
    }

    @Override
    protected void setActivityView() {
        setContentView(R.layout.tags_list_activity);
        ButterKnife.bind(this);
    }

    @Override
    public void assembleMenu() {
        addSortByDateMenu();
        addSortByAuthorMenu();
        addSortByCardMenuItem();
        makeSortingMenuVisible();
    }

    @Override
    public RecyclerView.ItemDecoration prepareItemDecoration(BasicViewMode viewMode) {
        return createItemDecoration(viewMode);
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected void setSomeItemSelectedViewState(SomeItemsSelectedViewState selectionViewState) {
        super.setSomeItemSelectedViewState(selectionViewState);

        boolean canDeleteComments = ((CommentsList_Presenter) mPresenter).canDeleteComments();
        boolean commentsCountIsDeletable = ((CommentsList_Presenter) mPresenter).commentsCountIsDeletable(selectionViewState.getSelectedItemsCount());

        if (canDeleteComments && commentsCountIsDeletable) {
            inflateMenu(R.menu.delete);
            makeMenuItemVisible(R.id.actionDelete, R.drawable.ic_delete);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (R.id.actionSortByCard == itemId) {
            mPresenter.onSortMenuItemClicked(eCommentsList_SortingMode.BY_CARD);
        }
        else if (R.id.actionSortByDate == itemId) {
            mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_DATE);
        }
        else if (R.id.actionSortByAuthor == itemId) {
            mPresenter.onSortMenuItemClicked(eCommentsList_SortingMode.BY_AUTHOR);
        }
        else if (R.id.actionDelete == itemId) {
            ((CommentsList_Presenter) mPresenter).onDeleteCommentsClicked();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    protected BasicMVPList_Presenter preparePresenter() {
        return BasicMVPList_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVPList_Presenter onPresenterPrepared() {
                return new CommentsList_Presenter(new ListViewMode(), eBasicSortingMode.BY_DATE);
            }
        });
    }

    @Override
    protected BasicMVPList_DataAdapter prepareDataAdapter() {
        return BasicMVPList_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVPList_DataAdapter onDataAdapterPrepared() {
                return new CommentsList_DataAdapter(mPresenter.getCurrentViewMode(), mPresenter);
            }
        });
    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.COMMENTS_LIST_page_title);
    }

    @Override
    public void setViewState(iViewState viewState) {
        if (viewState instanceof CommentsOfUser_ViewState)
            setCommentsOfUserViewState(viewState);
        else
            super.setViewState(viewState);
    }

    @Override
    public int getListScrollOffset() {
        return mRecyclerView.computeVerticalScrollOffset();
    }

    @Override
    public void setListScrollOffset(int verticalOffset) {
        mRecyclerView.scrollBy(0, verticalOffset);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void goShowCommentUnderCard(@NonNull Comment comment) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Constants.CARD_KEY, comment.getCardId());
        intent.putExtra(Constants.COMMENT_KEY, comment.getKey());
        startActivity(intent);
    }

    @Override
    public void goShowCommentedCard(@NonNull Comment comment) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Constants.CARD_KEY, comment.getCardId());
        startActivity(intent);
    }

    @Override
    public void goShowUserProfile(@NonNull String userId) {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    protected void processActivityResult() {
        switch (mActivityRequestCode) {
            /*case Constants.CODE_EDIT_TAG:
                processTagEditionResult(mActivityResultCode, mActivityResultData);
                break;*/
            default:
                super.processActivityResult();
                break;
        }
    }


    private void makeSortingMenuVisible() {
        makeMenuItemVisible(R.id.actionSort, R.drawable.ic_sort_visible);
    }

    private void addSortByAuthorMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_author)
                .addInflatedMenuItemId(R.id.actionSortByAuthor)
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCommentsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCommentsList_SortingMode.BY_AUTHOR.equals(sortingMode);
                    }
                })
                .create();
    }

    private void addSortByCardMenuItem() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_card)
                .addInflatedMenuItemId(R.id.actionSortByCard)
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCommentsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCommentsList_SortingMode.BY_CARD.equals(sortingMode);
                    }
                })
                .create();
    }


    private void setCommentsOfUserViewState(iViewState viewState) {
        CommentsOfUser_ViewState commentsOfUserViewState = (CommentsOfUser_ViewState) viewState;
        setNeutralViewState();
        setPageTitle(R.string.COMMENTS_LIST_comments_of_user_page_title,
                commentsOfUserViewState.getUserName());
    }
}
