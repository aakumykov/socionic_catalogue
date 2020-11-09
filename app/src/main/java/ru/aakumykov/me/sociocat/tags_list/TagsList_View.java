package ru.aakumykov.me.sociocat.tags_list;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_View;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.BasicMVP_Utils;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.RecyclerViewUtils;

public class TagsList_View extends BasicMVP_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list);
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
        makeSortingMenuVisible();
        addSortByCardsCountMenuItem();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (R.id.actionSortByCardsCountDirect == itemId || R.id.actionSortByCardsCountReverse == itemId) {
            mPresenter.onSortMenuItemClicked(eTagsList_SortingMode.CARDS_COUNT_DIRECT);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected BasicMVP_Presenter preparePresenter() {
        return BasicMVP_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVP_Presenter onPresenterPrepared() {
                return new TagsList_Presenter();
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
    protected RecyclerView.LayoutManager prepareLayoutManager() {
        return new LinearLayoutManager(this);
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


    private void makeSortingMenuVisible() {
        MenuItem sortMenuItem = mMenu.findItem(R.id.actionSort);
        if (null != sortMenuItem) {
            sortMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            sortMenuItem.setIcon(R.drawable.ic_sort_visible);
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
                            case CARDS_COUNT_DIRECT:
                            case CARDS_COUNT_REVERSE:
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
}
