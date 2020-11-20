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
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_View;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.BasicMVP_Utils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.RecyclerViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states.SelectionViewState;
import ru.aakumykov.me.sociocat.cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tag_edit.TagEdit_View;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_View;

public class TagsList_View extends BasicMVP_View implements iTagsList_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_activity);
        ButterKnife.bind(this);

        activateUpButton();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView.ItemDecoration itemDecoration =
                RecyclerViewUtils.createSimpleDividerItemDecoration(this, R.drawable.simple_list_item_divider);

        BasicMVP_Utils.configureRecyclerview(
                mRecyclerView,
                mDataAdapter,
                mLayoutManager,
                itemDecoration,
                null
        );
    }

    @Override
    public void compileMenu() {
        super.compileMenu();

        inflateMenu(R.menu.change_view_mode);

        makeSortingMenuVisible();
        addSortByCardsCountMenuItem();
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
        /*else if (R.id.actionSave == itemId) {
            scroll2position(10);
        }*/
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void setSelectedViewState(SelectionViewState viewState) {
        super.setSelectedViewState(viewState);
        inflateMenu(R.menu.tags_list_delete);
    }

    @Override
    protected BasicMVP_Presenter preparePresenter() {
        return BasicMVP_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVP_Presenter onPresenterPrepared() {
                return new TagsList_Presenter(eBasicViewMode.LIST, eBasicSortingMode.BY_NAME);
            }
        });
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
        return BasicMVP_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVP_DataAdapter onDataAdapterPrepared() {
                return new TagsList_DataAdapter(mPresenter);
            }
        });
    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.TAGS_LIST_page_title);
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
