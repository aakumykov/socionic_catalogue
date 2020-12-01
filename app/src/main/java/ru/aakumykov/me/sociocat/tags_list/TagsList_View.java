package ru.aakumykov.me.sociocat.tags_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_View;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.SelectionViewState;
import ru.aakumykov.me.sociocat.cards_list2.CardsList2_View;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tag_edit.TagEdit_View;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_View;

public class TagsList_View extends BasicMVPList_View implements iTagsList_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activateUpButton();
    }

    @Override
    protected void setActivityView() {
        setContentView(R.layout.tags_activity);
        ButterKnife.bind(this);
    }

    @Override
    public void assembleMenu() {
        addSearchView();

        addChangeViewModeMenu();

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

        if (R.id.actionSortByCardsCountDirect == itemId || R.id.actionSortByCardsCountReverse == itemId) {
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
    protected void setSelectedViewState(SelectionViewState viewState) {
        super.setSelectedViewState(viewState);

//        if (((TagsList_Presenter) mPresenter).canDeleteTag())
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
        Intent intent = new Intent(this, CardsList2_View.class);
        intent.setAction(Constants.ACTION_SHOW_CARDS_WITH_TAG);
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
                break;
        }
    }


    private void makeSortingMenuVisible() {
        if (null != mMenu) {
            MenuItem sortMenuItem = mMenu.findItem(R.id.actionSort);
            if (null != sortMenuItem) {
                sortMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                sortMenuItem.setIcon(R.drawable.ic_sort_visible);
            }
        }
    }

    private void addSortByCardsCountMenuItem() {

        new SortingMenuItemConstructor()
                .addMenuInflater(mMenuInflater)
                .addTargetMenu(mSortingSubmenu)
                .addMenuResource(R.menu.menu_sort_by_cards_count)
                .addDirectOrderMenuItemId(R.id.actionSortByCardsCountDirect)
                .addReverseOrderMenuItemId(R.id.actionSortByCardsCountReverse)
                .addDirectOrderActiveIcon(R.drawable.ic_menu_sort_by_cards_count)
                .addReverseOrderActiveIcon(R.drawable.ic_menu_sort_by_cards_count)
                .addDirectOrderInactiveIcon(R.drawable.ic_menu_sort_by_cards_count)
                .addSortingModeParamsCallback(new SortingMenuItemConstructor.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eTagsList_SortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        switch ((eTagsList_SortingMode) sortingMode) {
                            case BY_CARDS_COUNT:
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean isDirectOrder(eSortingOrder sortingOrder) {
                        return sortingOrder.isDirect();
                    }
                })
                .makeMenuItem(mPresenter.getCurrentSortingMode(), mPresenter.getCurrentSortingOrder());
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
