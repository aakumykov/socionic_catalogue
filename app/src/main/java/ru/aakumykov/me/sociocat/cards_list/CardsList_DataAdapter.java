package ru.aakumykov.me.sociocat.cards_list;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.aakumykov.me.sociocat.cards_list.utils.ItemsComparator;
import ru.aakumykov.me.sociocat.cards_list.utils.ItemsFilter;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.LoadMoreItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ThrobberItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.selectable_adapter.SelectableAdapter;
import ru.aakumykov.me.sociocat.cards_list.view_holders.BasicViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.DataItem_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.cards_list.view_holders.Unknown_ViewHolder;

public class CardsList_DataAdapter
        extends SelectableAdapter<RecyclerView.ViewHolder>
        implements iCardsList.iDataAdapter, Filterable
{
    private static final String TAG = CardsList_DataAdapter.class.getSimpleName();

    private iCardsList.iPresenter presenter;
    private iCardsList.ListEdgeReachedListener listEdgeReachedListener;

    private boolean isVirgin = true;
    private final List<ListItem> visibleItemsList = new ArrayList<>();
    private final List<ListItem> originalItemsList = new ArrayList<>();

    private ItemsFilter itemsFilter;
    private iCardsList.SortingMode currentSortingMode;


    // Конструктор
    public CardsList_DataAdapter() {

    }


    // RecyclerView.Adapter
    @Override
    public int getItemViewType(int position) {
        ListItem listItem = visibleItemsList.get(position);

        if (listItem instanceof DataItem)
            return iCardsList.DATA_ITEM_TYPE;
        else if (listItem instanceof LoadMoreItem)
            return iCardsList.LOADMORE_ITEM_TYPE;
        else if (listItem instanceof ThrobberItem)
            return iCardsList.THROBBER_ITEM_TYPE;
        else
            return iCardsList.UNKNOWN_VIEW_TYPE;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BasicViewHolder basicViewHolder =
                CardsList_ItemFactory.createViewHolder(viewType, parent, presenter.getViewMode());

        basicViewHolder.setPresenter(presenter);

        return basicViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = visibleItemsList.get(position);
        BasicViewHolder viewHolder;

        if (listItem instanceof DataItem) {
            viewHolder = (DataItem_ViewHolder) holder;
            setViewHolderViewState(viewHolder, position, listItem);
        }
        else if (listItem instanceof LoadMoreItem) {
            viewHolder = (LoadMore_ViewHolder) holder;
        }
        else if (listItem instanceof ThrobberItem) {
            viewHolder = (Throbber_ViewHolder) holder;
        }
        else {
            viewHolder = (Unknown_ViewHolder) holder;
        }

        viewHolder.initialize(listItem);

        // Достигнут конец списка
        if (position == (visibleItemsList.size() - 1)) {
            if (null != listEdgeReachedListener) {
                listEdgeReachedListener.onBottomReached(position);
                Log.d(TAG, "Достигнут низ страницы");
            }
        }
    }

    @Override
    public int getItemCount() {
        return visibleItemsList.size();
    }


    // iCardsList.iDataAdapter
    @Override
    public boolean isVirgin() {
        return this.isVirgin;
    }

    @Override
    public void setPresenter(iCardsList.iPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bindBottomReachedListener(iCardsList.ListEdgeReachedListener listener) {
        this.listEdgeReachedListener = listener;
    }

    @Override
    public void unbindBottomReachedListener() {
        this.listEdgeReachedListener = null;
    }

    @Override
    public void setList(List<DataItem> inputList) {
        visibleItemsList.clear();
        visibleItemsList.addAll(inputList);

        originalItemsList.clear();
        originalItemsList.addAll(inputList);

        this.isVirgin = false;

        notifyDataSetChanged();
    }

    @Override
    public void setFilteredList(List<DataItem> filteredList) {
        visibleItemsList.clear();
        visibleItemsList.addAll(filteredList);

        this.isVirgin = false; // На всякий случай

        notifyDataSetChanged();
    }

    @Override
    public void setListAndFilter(List<DataItem> inputList, CharSequence filterText) {
        originalItemsList.clear();
        originalItemsList.addAll(inputList);
        getFilter().filter(filterText);
    }

    @Override
    public void appendList(List<DataItem> inputList) {
        int startIndex = getMaxIndexVisible();

        originalItemsList.addAll(inputList);
        visibleItemsList.addAll(inputList);

        notifyItemRangeChanged(startIndex+1, inputList.size());
    }

    @Override
    public void appendListAndFilter(List<DataItem> inputList, CharSequence filterText) {
        originalItemsList.addAll(inputList);
        getFilter().filter(filterText);
    }

    @Override
    public DataItem getDataItem(int position) {
        if (position >= 0 && position <= getMaxIndexVisible()) {
            ListItem listItem = visibleItemsList.get(position);
            return (listItem instanceof DataItem) ? (DataItem) listItem : null;
        }
        else {
            return null;
        }
    }

    @Override
    public List<DataItem> getAllVisibleDataItems() {
        List<DataItem> dataItemsList = new ArrayList<>();

        for (ListItem listItem : visibleItemsList)
            if (listItem instanceof DataItem)
                dataItemsList.add((DataItem) listItem);

        return dataItemsList;
    }

    @Override
    public DataItem getLastOriginalDataItem() {
        int listSize = originalItemsList.size();
        int maxIndex = getMaxIndexOriginal();

        for (int i=0; i < listSize; i++) {
            ListItem listItem = originalItemsList.get(maxIndex - i);
            if (listItem instanceof DataItem)
                return (DataItem) listItem;
        }

        return null;
    }

    @Override
    public void removeItem(ListItem listItem) {
        int indexOrig = originalItemsList.indexOf(listItem);
        if (indexOrig >= 0)
            originalItemsList.remove(indexOrig);

        int indexVisible = visibleItemsList.indexOf(listItem);
        if (indexVisible >= 0) {
            visibleItemsList.remove(indexVisible);
            notifyItemRemoved(indexVisible);
        }
    }

    @Override
    public int getVisibleDataItemsCount() {
        return getAllVisibleDataItems().size();
    }

    @Override
    public void sortByName(iCardsList.SortingListener sortingListener) {
        if (null == currentSortingMode)
            currentSortingMode = iCardsList.SortingMode.ORDER_NAME_REVERSED;

        switch (currentSortingMode) {
            case ORDER_NAME_DIRECT:
                currentSortingMode = iCardsList.SortingMode.ORDER_NAME_REVERSED;
                break;
            default:
                currentSortingMode = iCardsList.SortingMode.ORDER_NAME_DIRECT;
                break;
        }

        performSorting(sortingListener);
    }

    @Override
    public void sortByDate(iCardsList.SortingListener sortingListener) {
        if (null == currentSortingMode)
            currentSortingMode = iCardsList.SortingMode.ORDER_COUNT_REVERSED;

        switch (currentSortingMode) {
            case ORDER_COUNT_REVERSED:
                currentSortingMode = iCardsList.SortingMode.ORDER_COUNT_DIRECT;
                break;
            default:
                currentSortingMode = iCardsList.SortingMode.ORDER_COUNT_REVERSED;
                break;
        }

        performSorting(sortingListener);
    }

    @Override
    public int getPositionOf(DataItem dataItem) {
        return visibleItemsList.indexOf(dataItem);
    }

    @Override
    public boolean allItemsAreSelected() {
        int dataItemsCount = getVisibleDataItemsCount();
        int selectedItemsCount = getSelectedItemsCount();
        return dataItemsCount == selectedItemsCount && dataItemsCount > 0;
    }

    @Override
    public void showLoadmoreItem() {
        ListItem endingItem = getVisibleEndingItem();

        if (endingItem instanceof LoadMoreItem)
            return;

        if (endingItem instanceof ThrobberItem) {
            replaceVisibleEndingItem(new LoadMoreItem());
        } else {
            appendItem(new LoadMoreItem());
        }
    }

    @Override
    public void showThrobberItem() {
        ListItem endingItem = getVisibleEndingItem();

        if (endingItem instanceof ThrobberItem)
            return;

        if (endingItem instanceof LoadMoreItem)
            replaceVisibleEndingItem(new ThrobberItem());
        else
            appendItem(new ThrobberItem());
    }

    @Override
    public void hideLoadmoreItem() {
        ListItem endingItem = getVisibleEndingItem();

        if (endingItem instanceof LoadMoreItem)
            removeItem(endingItem);
    }

    @Override
    public void hideThrobberItem() {
        ListItem endingItem = getVisibleEndingItem();

        if (endingItem instanceof ThrobberItem)
            removeItem(endingItem);
    }

    @Override
    public List<DataItem> getSelectedItems() {
        List<Integer> selectedIndexes = getSelectedIndexes();
        List<DataItem> selectedItems = new ArrayList<>();
        for (int index : selectedIndexes)
            selectedItems.add(getDataItem(index));
        return selectedItems;
    }

    @Override
    public void setLayoutMode(iCardsList.ViewMode viewMode) {
        presenter.storeViewMode(viewMode);
        notifyDataSetChanged();
    }

    @Override
    public void setItemIsNowDeleting(DataItem dataItem, boolean value) {
        dataItem.setIsNowDeleting(value);
        notifyItemChanged(visibleItemsList.indexOf(dataItem));
    }

    @Override
    public int addItem(@NonNull DataItem dataItem) {
        visibleItemsList.add(0, dataItem);
        originalItemsList.add(0, dataItem);
        notifyItemInserted(0);
        return 0;
    }

    @Override
    public void updateItem(@NonNull DataItem dataItem) {
        int index = visibleItemsList.indexOf(dataItem);
        visibleItemsList.set(index, dataItem);
        notifyItemChanged(index);
    }

    @Override
    public void updateItemWithCard(@NonNull Card newCard) {
        int position = -1;

        position = findCardPosition(newCard, originalItemsList);
        if (position >= 0)
            originalItemsList.set(position, new DataItem<>(newCard));

        position = findCardPosition(newCard, visibleItemsList);
        if (position >= 0) {
            visibleItemsList.set(position, new DataItem<>(newCard));
            notifyItemChanged(position);
        }
    }

    @Override
    public void deleteItemWithCard(@NonNull Card card) {
        int position = -1;

        position = findCardPosition(card, originalItemsList);
        if (position >= 0)
            originalItemsList.remove(position);

        position = findCardPosition(card, visibleItemsList);
        if (position >= 0) {
            visibleItemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private int findCardPosition(@NonNull Card card, List<ListItem> targetList) {
        for (ListItem listItem : targetList) {
            if (listItem instanceof DataItem) {
                Card cardInItem = (Card) ((DataItem) listItem).getPayload();
                if (cardInItem.getKey().equals(card.getKey())) {
                    return targetList.indexOf(listItem);
                }
            }
        }
        return -1;
    }

    @Override
    public void filterList(CharSequence filterText) {
        getFilter().filter(filterText);
    }


    // Filterable
    @Override
    public Filter getFilter() {
        if (null == itemsFilter)
            this.itemsFilter = new ItemsFilter(presenter);

        itemsFilter.setList(originalItemsList);

        return itemsFilter;
    }


    // Внутренние методы
    private void setViewHolderViewState(BasicViewHolder dataItemViewHolder, int position, ListItem listItem) {

        if (isSelected(position))
            dataItemViewHolder.setViewState(iCardsList.ItemState.SELECTED);

        else if (listItem.isNowDeleting())
            dataItemViewHolder.setViewState(iCardsList.ItemState.DELETING);

        else
            dataItemViewHolder.setViewState(iCardsList.ItemState.NEUTRAL);
    }

    private void appendItem(ListItem listItem) {
        visibleItemsList.add(listItem);
        notifyItemInserted(visibleItemsList.indexOf(listItem));
    }

    private int getMaxIndexVisible() {
        int size = visibleItemsList.size();

        if (0 == size)
            return -1;

        return size - 1;
    }

    private int getMaxIndexOriginal() {
        int size = originalItemsList.size();

        if (0 == size)
            return -1;

        return size - 1;
    }

    private ListItem getVisibleEndingItem() {
        int maxIndex = getMaxIndexVisible();

        if (maxIndex < 0)
            return null;

        return visibleItemsList.get(maxIndex);
    }

    private void replaceVisibleEndingItem(ListItem replacementItem) {
        ListItem endingItem = getVisibleEndingItem();
        if (null != endingItem) {
            int index = visibleItemsList.indexOf(endingItem);
            visibleItemsList.set(index, replacementItem);
            notifyItemChanged(index);
        }
    }

    private void performSorting(@Nullable iCardsList.SortingListener sortingListener) {
        Collections.sort(visibleItemsList, new ItemsComparator(currentSortingMode));

        notifyDataSetChanged();

        if (null != sortingListener)
            sortingListener.onSortingComplete();
    }
}















