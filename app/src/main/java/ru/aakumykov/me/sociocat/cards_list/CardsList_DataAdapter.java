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

import ru.aakumykov.me.sociocat.cards_list.filter_stuff.ItemsComparator;
import ru.aakumykov.me.sociocat.cards_list.filter_stuff.ItemsFilter;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItemsFactory;
import ru.aakumykov.me.sociocat.cards_list.list_items.LoadMoreItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ThrobberItem;
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
    private volatile List<ListItem> itemsList = new ArrayList<>();

    private ItemsFilter itemsFilter;
    private iCardsList.SortingMode currentSortingMode = iCardsList.SortingMode.ORDER_NAME_DIRECT;


    // Конструктор
    public CardsList_DataAdapter(iCardsList.iPresenter presenter) {

        if (null == presenter)
            throw new IllegalArgumentException("Presenter passed as argument cannot be null");

        this.presenter = presenter;
    }


    // RecyclerView.Adapter
    @Override
    public int getItemViewType(int position) {
        ListItem listItem = itemsList.get(position);

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
        BasicViewHolder basicViewHolder = ListItemsFactory.createViewHolder(viewType, parent);
        basicViewHolder.setPresenter(presenter);
        return basicViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = itemsList.get(position);
        BasicViewHolder viewHolder;

        if (listItem instanceof DataItem) {
            viewHolder = (DataItem_ViewHolder) holder;
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
        viewHolder.setSelected(isSelected(position));

        // Достигнут конец списка
        if (position == (itemsList.size() - 1)) {
            if (null != listEdgeReachedListener) {
                listEdgeReachedListener.onBottomReached(position);
                Log.d(TAG, "Достигнут низ страницы");
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    // iCardsList.iDataAdapter
    @Override
    public boolean isVirgin() {
        return this.isVirgin;
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
        itemsList.clear();
        itemsList.addAll(inputList);

        this.isVirgin = false;

        performSorting(null);
    }

    @Override
    public void setList(List<DataItem> inputList, CharSequence filterQuery) {
        itemsList.clear();
        itemsList.addAll(inputList);

        this.isVirgin = false;

        getFilter().filter(filterQuery);

        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<DataItem> inputList) {
        int startIndex = getMaxIndex();

        itemsList.addAll(inputList);

        notifyItemRangeChanged(startIndex+1, inputList.size());
    }

    @Override
    public DataItem getDataItem(int position) {
        if (position >= 0 && position <= getMaxIndex()) {
            ListItem listItem = itemsList.get(position);
            return (listItem instanceof DataItem) ? (DataItem) listItem : null;
        }
        else {
            return null;
        }
    }

    @Override
    public List<ListItem> getAllItems() {
        return itemsList;
    }

    @Override
    public DataItem getLastDataItem() {
        int listSize = itemsList.size();
        int maxIndex = getMaxIndex();

        for (int i=0; i < listSize; i++) {
            ListItem listItem = itemsList.get(maxIndex - i);
            if (listItem instanceof DataItem)
                return (DataItem) listItem;
        }

        return null;
    }

    @Override
    public void removeItem(ListItem listItem) {
        int index = itemsList.indexOf(listItem);
        if (index >= 0) {
            itemsList.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getListSize() {
        return itemsList.size();
    }

    @Override
    public void sortByName(iCardsList.SortingListener sortingListener) {
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
    public void sortByCount(iCardsList.SortingListener sortingListener) {
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
    public iCardsList.SortingMode getSortingMode() {
        return currentSortingMode;
    }

    @Override
    public int getPositionOf(DataItem dataItem) {
        return itemsList.indexOf(dataItem);
    }

    @Override
    public boolean allItemsAreSelected() {
        int itemsCount = getItemCount();
        int selectedItemsCount = getSelectedItemCount();
        return itemsCount > 0 && itemsCount == selectedItemsCount;
    }

    @Override
    public void showLoadmoreItem() {
        ListItem endingItem = getEndingItem();

        if (endingItem instanceof LoadMoreItem)
            return;

        if (endingItem instanceof ThrobberItem)
            replaceEndingItem(new LoadMoreItem());
        else
            addItem(new LoadMoreItem());
    }

    @Override
    public void showThrobberItem() {
        ListItem endingItem = getEndingItem();

        if (endingItem instanceof ThrobberItem)
            return;

        if (endingItem instanceof LoadMoreItem)
            replaceEndingItem(new ThrobberItem());
        else
            addItem(new ThrobberItem());
    }

    @Override
    public void hideThrobberItem() {
        ListItem endingItem = getEndingItem();

        if (endingItem instanceof ThrobberItem)
            removeItem(endingItem);
    }

    private void addItem(ListItem listItem) {
        itemsList.add(listItem);
        int index = itemsList.indexOf(listItem);
        notifyItemInserted(index);
    }

    @Override
    public List<DataItem> getSelectedItems() {
        List<Integer> selectedIndexes = getSelectedIndexes();
        List<DataItem> selectedItems = new ArrayList<>();
        for (int index : selectedIndexes)
            selectedItems.add(getDataItem(index));
        return selectedItems;
    }


    // Filterable
    @Override
    public Filter getFilter() {
        if (null == itemsFilter)
            itemsFilter = new ItemsFilter(itemsList, presenter);
        return itemsFilter;
    }


    // Внутренние методы
    private int getMaxIndex() {
        int size = itemsList.size();

        if (0 == size)
            return -1;

        return size - 1;
    }

    private ListItem getEndingItem() {
        int maxIndex = getMaxIndex();

        if (maxIndex < 0)
            return null;

        return itemsList.get(maxIndex);
    }

    private void replaceEndingItem(ListItem replacementItem) {
        ListItem endingItem = getEndingItem();
        if (null != endingItem) {
            int index = itemsList.indexOf(endingItem);
            itemsList.set(index, replacementItem);
            notifyItemChanged(index);
        }
    }

    private void performSorting(@Nullable iCardsList.SortingListener sortingListener) {
        Collections.sort(itemsList, new ItemsComparator(currentSortingMode));

        notifyDataSetChanged();

        if (null != sortingListener)
            sortingListener.onSortingComplete();
    }
}
