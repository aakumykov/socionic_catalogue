package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.CardType;
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

    enum ItemState {
        SELECTED,
        DELETING,
        NEUTRAL
    }

    enum ViewMode {
        FEED,
        LIST,
        GRID
    }

    enum PageViewState {
        SUCCESS,
        PROGRESS,
        REFRESHING,
        SELECTION,
        ERROR
    }

    enum ToolbarState {
        INITIAL,
        SORTING,
        FILTERING
    }

    enum SortingMode {
        ORDER_NAME_DIRECT,
        ORDER_NAME_REVERSED,
        ORDER_COUNT_DIRECT,
        ORDER_COUNT_REVERSED
    }


    interface iPageView extends iBaseView {

        void changeViewMode(@NonNull ViewMode viewMode);

        void setViewState(PageViewState pageViewState, Integer messageId, @Nullable Object messageDetails);

        void setToolbarState(ToolbarState toolbarState);

        boolean actionModeIsActive();
        void finishActionMode();

        void scrollToPosition(int position);

        void goShowCard(Card card);
        void goEditCard(Card card);

        void showAddNewCardMenu();

        void goCreateCard(CardType cardType);

        void goUserProfile(String userId);

        void go2cardComments(Card card);
    }

    interface iDataAdapter extends Filterable, iSelectableAdapter {

        void setPresenter(iPresenter presenter);

        void bindBottomReachedListener(ListEdgeReachedListener listener);
        void unbindBottomReachedListener();

        boolean isVirgin();

        void setList(List<DataItem> inputList);
        void setFilteredList(List<DataItem>filteredList);
        void appendList(List<DataItem> inputList);

        DataItem getDataItem(int position);
        List<DataItem> getAllVisibleDataItems();
        DataItem getLastOriginalDataItem();

        void removeItem(ListItem listItem);

        int getVisibleDataItemsCount();

        void sortByName(SortingListener sortingListener);
        void sortByDate(SortingListener sortingListener);

        int getPositionOf(DataItem dataItem);

        boolean allItemsAreSelected();

        void showLoadmoreItem();
        void showThrobberItem();

        void hideLoadmoreItem();
        void hideThrobberItem();

        List<DataItem> getSelectedItems();

        void setLayoutMode(ViewMode viewMode);

        void setItemIsNowDeleting(DataItem dataItem, boolean value);

        int addItem(@NonNull DataItem dataItem);
        void updateItem(@NonNull DataItem currentlyEditedItem);
    }

    interface iPresenter {
        void setDataAdapter(iDataAdapter dataAdapter);

        void linkView(iPageView pageView);
        void unlinkView();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigurationChanged();

	    void storeViewState(PageViewState pageViewState, Integer messageId, Object messageDetails);

        void storeViewMode(ViewMode initialViewMode);
        ViewMode getViewMode();

	    void storeToolbarState(ToolbarState toolbarState);
	    ToolbarState getToolbarState();

        void onRefreshRequested();

        void onDataItemClicked(DataItem dataItem);
        void onDataItemLongClicked(DataItem dataItem);

        void onCardAuthorClicked(String userId);
        void onCardCommentsClicked(Card card);
        void onRatingWidgetClicked(Card card);

        void onLoadMoreClicked();

        void onListFiltered(CharSequence filterText, List<DataItem> filteredList);

        boolean hasFilterText();
        CharSequence getFilterText();

        boolean canSelectItem();
        boolean canEditSelectedItem();
        boolean canDeleteSelectedItem();

        void onSelectAllClicked();
        void onClearSelectionClicked();
        void onEditSelectedItemClicked();
        void onDeleteSelectedItemsClicked();

        void onActionModeDestroyed();

        void onChangeLayoutClicked();

        void onNewCardMenuClicked();
        void onNewCardTypeSelected(CardType cardType);

        void onNewCardCreated(@Nullable Intent data);
        void onCardEdited(@Nullable Intent data);

        boolean isLoggedIn();
    }


    interface SortingListener {
        void onSortingComplete();
    }

    interface ListEdgeReachedListener {
        void onTopReached(int position);
        void onBottomReached(int position);
    }
}
