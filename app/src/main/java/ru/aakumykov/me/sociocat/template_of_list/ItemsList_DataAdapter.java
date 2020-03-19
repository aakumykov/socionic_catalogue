package ru.aakumykov.me.sociocat.template_of_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.filter_stuff.ItemsComparator;
import ru.aakumykov.me.sociocat.template_of_list.filter_stuff.ItemsFilter;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.view_holders.DataItem_ViewHolder;

public class ItemsList_DataAdapter
        extends SelectableAdapter<RecyclerView.ViewHolder>
        implements iItemsList.iDataAdapter, Filterable
{
    private iItemsList.iPresenter presenter;

    private boolean isVirgin = true;
    private List<DataItem> itemsList = new ArrayList<>();

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
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.template_of_list_data_item, parent, false);
        return new DataItem_ViewHolder(itemView, presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DataItem dataItem = itemsList.get(position);

        DataItem_ViewHolder itemViewHolder = (DataItem_ViewHolder) holder;

        itemViewHolder.initialize(dataItem);

        itemViewHolder.setSelected(isSelected(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
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
            return itemsList.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public List<DataItem> getAllItems() {
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
