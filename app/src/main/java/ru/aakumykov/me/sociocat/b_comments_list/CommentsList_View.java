package ru.aakumykov.me.sociocat.b_comments_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_View;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_View;
import ru.aakumykov.me.sociocat.b_comments_list.view_states.CommentsOfUser_ViewState;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.user_show.UserShow_View;

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
//        addSearchView();
//        addSortByNameMenu();
//        addSortByCardsCountMenuItem();
//        makeSortingMenuVisible();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        /*int itemId = item.getItemId();

        if (R.id.actionSortByCardsCount == itemId) {
            mPresenter.onSortMenuItemClicked(eCommentsList_SortingMode.BY_CARDS_COUNT);
        }
        else if (R.id.actionDelete == itemId) {
            ((CommentsList_Presenter) mPresenter).onDeleteMenuItemClicked();
        }
        else {
            return super.onOptionsItemSelected(item);
        }*/

        return false;
    }

    /*@Override
    protected void setSomeItemSelectedViewState(SomeItemsSelectedViewState viewState) {
        super.setSomeItemSelectedViewState(viewState);

        if (((CommentsList_Presenter) mPresenter).canDeleteTag())
            inflateMenu(R.menu.tags_list_delete);
    }*/

    @Override
    protected BasicMVPList_Presenter preparePresenter() {
        return BasicMVPList_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVPList_Presenter onPresenterPrepared() {
                return new CommentsList_Presenter(new ListViewMode(), eBasicSortingMode.BY_NAME);
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

    private void addSortByCardsCountMenuItem() {

        /*addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_cards_count)
                .addInflatedMenuItemId(R.id.actionSortByCardsCount)
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eCommentsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eCommentsList_SortingMode.BY_CARDS_COUNT.equals(sortingMode);
                    }
                })
                .create();*/
    }

    /*private void processTagEditionResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK == resultCode) {
            if (null != data) {
                Tag oldTag = data.getParcelableExtra(Constants.OLD_TAG);
                Tag newTag = data.getParcelableExtra(Constants.NEW_TAG);

                ((CommentsList_Presenter) mPresenter).onTagEdited(oldTag, newTag);
            }
        }
    }*/

    private void setCommentsOfUserViewState(iViewState viewState) {
        CommentsOfUser_ViewState commentsOfUserViewState = (CommentsOfUser_ViewState) viewState;
        setNeutralViewState();
        setPageTitle(R.string.COMMENTS_LIST_comments_of_user_page_title,
                commentsOfUserViewState.getUserName());
    }
}
