package ru.aakumykov.me.sociocat.c_tags_list;

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
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.builders.SortingMenuItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.SomeItemsSelectedViewState;
import ru.aakumykov.me.sociocat.b_cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.c_tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.c_tags_list.interfaces.iTagsList_View;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tag_edit.TagEdit_View;

public class TagsList_View extends BasicMVPList_View implements iTagsList_View {

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
        addSearchView();
        addSortByNameMenu();
        addSortByCardsCountMenuItem();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (R.id.actionSortByCardsCount == itemId) {
            mPresenter.onSortMenuItemClicked(eTagsList_SortingMode.BY_CARDS_COUNT);
        }
        else if (R.id.actionDelete == itemId) {
            ((TagsList_Presenter) mPresenter).onDeleteMenuItemClicked();
        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void setSomeItemSelectedViewState(SomeItemsSelectedViewState viewState) {
        super.setSomeItemSelectedViewState(viewState);

        if (((TagsList_Presenter) mPresenter).canDeleteTag())
            inflateMenu(R.menu.tags_list_delete);
    }

    @Override
    protected BasicMVPList_Presenter preparePresenter() {
        return BasicMVPList_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVPList_Presenter onPresenterPrepared() {
                return new TagsList_Presenter(new ListViewMode(), eBasicSortingMode.BY_NAME);
            }
        });
    }

    @Override
    protected BasicMVPList_DataAdapter prepareDataAdapter() {
        return BasicMVPList_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVPList_DataAdapter onDataAdapterPrepared() {
                return new TagsList_DataAdapter(mPresenter.getCurrentViewMode(), mPresenter);
            }
        });
    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.TAGS_LIST_page_title);
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
    public void goShowCardsWithTag(@NonNull Tag tag) {
        Intent intent = new Intent(this, CardsList_View.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Constants.TAG_NAME, tag.getName());
        startActivity(intent);
    }

    @Override
    public void goEditTag(@NonNull Tag tag) {
        Intent intent = new Intent(this, TagEdit_View.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Constants.TAG_NAME, tag.getName());
        startActivityForResult(intent, Constants.CODE_EDIT_TAG);
    }


    @Override
    protected void processActivityResult() {
        switch (mActivityRequestCode) {
            case Constants.CODE_EDIT_TAG:
                processTagEditionResult(mActivityResultCode, mActivityResultData);
                break;
            default:
                super.processActivityResult();
                break;
        }
    }


    private void makeSortingMenuVisible() {
        makeMenuItemVisible(R.id.actionSort, R.drawable.ic_sort_visible);
    }

    private void addSortByCardsCountMenuItem() {

        addSortingMenuRootIfNotExists();

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
                        return sortingMode instanceof eTagsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eTagsList_SortingMode.BY_CARDS_COUNT.equals(sortingMode);
                    }
                })
                .create();
    }

    private void processTagEditionResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK == resultCode) {
            if (null != data) {
                Tag oldTag = data.getParcelableExtra(Constants.OLD_TAG);
                Tag newTag = data.getParcelableExtra(Constants.NEW_TAG);

                ((TagsList_Presenter) mPresenter).onTagEdited(oldTag, newTag);
            }
        }
    }

}
