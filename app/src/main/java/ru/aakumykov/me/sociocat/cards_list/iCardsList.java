package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.selectable_adapter.iSelectableAdapter;

public interface iCardsList {

    int DATA_ITEM_TYPE = 10;
    int LOADMORE_ITEM_TYPE = 20;
    int THROBBER_ITEM_TYPE = 30;
    int UNKNOWN_VIEW_TYPE = -1;

    enum ItemType {
        DATA_ITEM,
        LOADMORE_ITEM,
        THROBBER_ITEM
    }

    enum LayoutMode {
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

        void changeLayout(LayoutMode layoutMode);

        boolean actionModeIsActive();
        void finishActionMode();

        void scrollToPosition(int position);

        void goShowCard(Card card);
    }

    interface iDataAdapter extends Filterable, iSelectableAdapter {

        void bindBottomReachedListener(ListEdgeReachedListener listener);
        void unbindBottomReachedListener();

        boolean isVirgin();

        void setList(List<DataItem> inputList);
        void setList(List<DataItem> inputList, CharSequence filterQuery);
        void appendList(List<DataItem> inputList);

        DataItem getDataItem(int position);
        List<DataItem> getAllDataItems();
        DataItem getLastDataItem();

        void removeItem(ListItem listItem);

        int getDataItemsCount();

        void sortByName(SortingListener sortingListener);
        void sortByCount(SortingListener sortingListener);
        SortingMode getSortingMode();

        int getPositionOf(DataItem dataItem);

        boolean allItemsAreSelected();

        void showLoadmoreItem();
        void showThrobberItem();

        void hideLoadmoreItem();
        void hideThrobberItem();

        List<DataItem> getSelectedItems();

        void setLayoutMode(LayoutMode currentLayoutMode);

        void setItemIsNowDeleting(DataItem dataItem, boolean value);
    }

    interface iPresenter {
        void linkViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unlinkViewAndAdapter();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();

        void storeViewState(ViewState viewState, Integer messageId, Object messageDetails);

        LayoutMode getCurrentLayoutMode();

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

        void onChangeLayoutClicked();
    }


    interface SortingListener {
        void onSortingComplete();
    }

    interface ListEdgeReachedListener {
        void onTopReached(int position);
        void onBottomReached(int position);
    }
}
