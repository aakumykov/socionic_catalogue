package ru.aakumykov.me.sociocat.template_of_list;

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

import ru.aakumykov.me.sociocat.template_of_list.filter_stuff.ItemsComparator;
import ru.aakumykov.me.sociocat.template_of_list.filter_stuff.ItemsFilter;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.model.ListItem;
import ru.aakumykov.me.sociocat.template_of_list.model.LoadMoreItem;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.BasicViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.DataItem_ViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.Unknown_ViewHolder;

public class ItemsList_DataAdapter
        extends SelectableAdapter<RecyclerView.ViewHolder>
        implements iItemsList.iDataAdapter, Filterable
{
    private static final String TAG = ItemsList_DataAdapter.class.getSimpleName();

    private iItemsList.iPresenter presenter;
    private iItemsList.ListEdgeReachedListener listEdgeReachedListener;

    private boolean isVirgin = true;
    private List<ListItem> itemsList = new ArrayList<>();

    private ItemsFilter itemsFilter;
    private iItemsList.SortingMode currentSortingMode = iItemsList.SortingMode.ORDER_NAME_DIRECT;


    // Конструктор
    public ItemsList_DataAdapter(iItemsList.iPresenter presenter) {

        if (null == presenter)
            throw new IllegalArgumentException("Presenter passed as argument cannot be null");

        this.presenter = presenter;
    }


    // RecyclerView
    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }


    // RecyclerView.Adapter
    @Override
    public int getItemViewType(int position) {
        ListItem listItem = itemsList.get(position);

        if (listItem instanceof DataItem)
            return iItemsList.DATA_ITEM_TYPE;
        else if (listItem instanceof LoadMoreItem)
            return iItemsList.LOADMORE_ITEM_TYPE;
        else
            return iItemsList.UNKNOWN_VIEW_TYPE;
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

    @Override
    public void bindBottomReachedListener(iItemsList.ListEdgeReachedListener listener) {
        this.listEdgeReachedListener = listener;
    }

    @Override
    public void unbindBottomReachedListener() {
        this.listEdgeReachedListener = null;
    }


    // iItemsList.iDataAdapter
    @Override
    public boolean isVirgin() {
        return this.isVirgin;
    }

    @Override
    public void setList(List<DataItem> inputList) {
        itemsList.clear();
        itemsList.addAll(inputList);

        itemsList.add(new LoadMoreItem());

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
        int startIndex = maxIndex();
        itemsList.addAll(inputList);
        notifyItemRangeChanged(startIndex, inputList.size());
    }

    @Override
    public DataItem getItem(int position) {
        if (position >= 0 && position <= maxIndex()) {
            return (DataItem) itemsList.get(position);
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
    public void removeItem(DataItem dataItem) {
        int index = itemsList.indexOf(dataItem);
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
    public void sortByName(iItemsList.SortingListener sortingListener) {
        switch (currentSortingMode) {
            case ORDER_NAME_DIRECT:
                currentSortingMode = iItemsList.SortingMode.ORDER_NAME_REVERSED;
                break;
            default:
                currentSortingMode = iItemsList.SortingMode.ORDER_NAME_DIRECT;
                break;
        }

        performSorting(sortingListener);
    }

    @Override
    public void sortByCount(iItemsList.SortingListener sortingListener) {
        switch (currentSortingMode) {
            case ORDER_COUNT_REVERSED:
                currentSortingMode = iItemsList.SortingMode.ORDER_COUNT_DIRECT;
                break;
            default:
                currentSortingMode = iItemsList.SortingMode.ORDER_COUNT_REVERSED;
                break;
        }

        performSorting(sortingListener);
    }

    @Override
    public iItemsList.SortingMode getSortingMode() {
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


    // Filterable
    @Override
    public Filter getFilter() {
        if (null == itemsFilter)
            itemsFilter = new ItemsFilter(itemsList, presenter);
        return itemsFilter;
    }


    // Внутренние методы
    private int maxIndex() {
        return itemsList.size() - 1;
    }

    private void performSorting(@Nullable iItemsList.SortingListener sortingListener) {
        Collections.sort(itemsList, new ItemsComparator(currentSortingMode));

        notifyDataSetChanged();

        if (null != sortingListener)
            sortingListener.onSortingComplete();
    }
}
