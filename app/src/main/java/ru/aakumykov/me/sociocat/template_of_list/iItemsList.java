package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.model.ListItem;

public interface iItemsList {

    int DATA_ITEM_TYPE = 10;
    int LOADMORE_ITEM_TYPE = 20;
    int THROBBER_ITEM_TYPE = 30;
    int UNKNOWN_VIEW_TYPE = -1;

    enum ItemType {
        DATA_ITEM,
        LOADMORE_ITEM,
        THROBBER_ITEM
    }

    enum LayoutType {
        LIST,
        GRID
    }

    enum ViewState {
        SUCCESS,
        PROGRESS,
        REFRESHING,
        SELECTION,
        ERROR
    }

    enum SortingMode {
        ORDER_NAME_DIRECT,
        ORDER_NAME_REVERSED,
        ORDER_COUNT_DIRECT,
        ORDER_COUNT_REVERSED
    }


    interface iPageView extends iBaseView {
        void setViewState(ViewState viewState, Integer messageId, @Nullable Object messageDetails);
        void setLayoutType(LayoutType layoutType);
        boolean actionModeIsActive();
    }

    interface iDataAdapter extends Filterable, iSelectableAdapter {

        void bindBottomReachedListener(ListEdgeReachedListener listener);
        void unbindBottomReachedListener();

        boolean isVirgin();

        void setList(List<DataItem> inputList);
        void setList(List<DataItem> inputList, CharSequence filterQuery);
        void appendList(List<DataItem> inputList);

        DataItem getItem(int position);
        List<ListItem> getAllItems();

        void removeItem(ListItem listItem);

        int getListSize();

        void sortByName(SortingListener sortingListener);
        void sortByCount(SortingListener sortingListener);
        SortingMode getSortingMode();

        int getPositionOf(DataItem dataItem);

        boolean allItemsAreSelected();

        void showLoadmoreItem();
        void hideLoadmoreItem();

        void showThrobberItem();
        void hideThrobberItem();
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkViewAndAdapter();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();

        void storeViewState(ViewState viewState, Integer messageId, Object messageDetails);

        void storeLayoutType(LayoutType layoutType);
        LayoutType getLayoutType();

        void onRefreshRequested();

        void onItemClicked(DataItem dataItem);
        void onItemLongClicked(DataItem dataItem);
        void onLoadMoreClicked();

        void onListFiltered(CharSequence filterText, List<DataItem> filteredList);

        boolean hasFilterText();
        CharSequence getFilterText();

        boolean canSelectAll();
        boolean canEditSelectedItem();
        boolean canDeleteSelectedItem();

        void onSelectAllClicked();
        void onClearSelectionClicked();
        void onEditSelectedItemClicked();
        void onDeleteSelectedItemsClicked();

        void onActionModeDestroyed();
    }


    interface SortingListener {
        void onSortingComplete();
    }

    interface ListEdgeReachedListener {
        void onTopReached(int position);
        void onBottomReached(int position);
    }
}
