package ru.aakumykov.me.sociocat.a_basic_mvp_components;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicFilterableLsit;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasic_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_LoadmoreItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ThrobberItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils.Basic_ItemsFilter;


public abstract class Basic_DataAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements iBasicList
{
    private boolean mIsVirgin = true;
    private boolean mIsSorted = false;

    private final List<Basic_ListItem> mItemsList;

    private String mFilterPattern;
    private final List<Basic_ListItem> mOriginalItemsList;

    protected Basic_ViewHolderCreator mViewHolderCreator;
    protected Basic_ViewHolderBinder mViewHolderBinder;
    protected Basic_ViewTypeDetector mViewTypeDetector;

    protected iBasic_ItemClickListener mItemClickListener;
    private Filter mFilter;

    private int mSelectedItemsCount;


    public Basic_DataAdapter(
            iBasic_ItemClickListener itemClickListener
    )
    {
        mItemClickListener = itemClickListener;

        mItemsList = new ArrayList<>();
        mOriginalItemsList = new ArrayList<>();

        mViewHolderCreator = prepareViewHolderCreator();
        mViewHolderBinder = prepareViewHolderBinder();
        mViewTypeDetector = prepareViewTypeDetector();
    }


    protected abstract Basic_ViewHolderCreator prepareViewHolderCreator();
    protected abstract Basic_ViewHolderBinder prepareViewHolderBinder();
    protected abstract Basic_ViewTypeDetector prepareViewTypeDetector();
    protected abstract iItemsComparator getItemsComparator();


    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mViewHolderCreator.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Basic_ListItem listItem = getItem(position);
        mViewHolderBinder.bindViewHolder(holder, position, listItem);
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypeDetector.getItemType(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    @Override
    public boolean isVirgin() {
        return mIsVirgin;
    }

    @Override
    public boolean isSorted() {
        return mIsSorted;
    }

    @Override
    public void setList(List<Basic_ListItem> inputList) {
        mIsVirgin = false;

        mItemsList.clear();
        mItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void setListAndFilter(List<Basic_ListItem> inputList) {
        mIsVirgin = false;

        mOriginalItemsList.clear();
        mOriginalItemsList.addAll(inputList);
        applyFilter(mFilterPattern);

//        mItemsList.clear();
//        mItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<Basic_ListItem> inputList) {
        appendListAndSort(inputList, null, null);
    }

    @Override
    public void appendListAndSort(List<Basic_ListItem> inputList, @Nullable iSortingMode sortingMode, @Nullable eSortingOrder sortingOrder) {
        hideThrobberItem();
        hideLoadmoreItem();

        int insertPosition = getMaxIndex() + 1;
        mItemsList.addAll(inputList);

        if (isSorted())
            if (null != sortingMode && null != sortingOrder)
                sortList(sortingMode, sortingOrder);

        notifyItemRangeInserted(insertPosition, inputList.size());
    }

    @Override
    public void appendListAndFilter(List<Basic_ListItem> inputList) {
        addToOriginalItemsList(inputList);
        applyFilter();
    }

    @Override
    public void addItem(Basic_ListItem listItem) {
        mItemsList.add(listItem);
        notifyItemInserted(getMaxIndex());
    }

    @Override
    public int getAllItemsCount() {
//        return (isFiltered()) ? mOriginalItemsList.size() : mItemsList.size();
        int size = 0;

        if (isFiltered())
            size = mOriginalItemsList.size();
        else
            size = mItemsList.size();

        return size;
    }

    @Override
    public int getDataItemsCount() {
        int count = getAllItemsCount();

        ListIterator<Basic_ListItem> listIterator = (isFiltered()) ?
                mOriginalItemsList.listIterator(count) : mItemsList.listIterator(count);

        Basic_ListItem listItem = listIterator.previous();

        while (null != listItem) {
            if (listItem instanceof Basic_DataItem)
                return count;

            count--;
            listItem = listIterator.previous();
        }

        return 0;
    }

    @Override
    public Basic_ListItem getItem(int position) {
        return mItemsList.get(position);
    }

    @Override
    public Basic_DataItem getLastDataItem() {
        ListIterator<Basic_ListItem> listIterator = mItemsList.listIterator(mItemsList.size());
        Basic_ListItem listItem;
        while (listIterator.hasPrevious()) {
            listItem = listIterator.previous();
            if (listItem instanceof Basic_DataItem)
                return (Basic_DataItem) listItem;
        }
        return null;
    }

    @Override
    public void refreshItem(int position) {
        notifyItemChanged(position);
    }


    @Override
    public void showThrobberItem() {
        addItem(new Basic_ThrobberItem());
    }

    @Override
    public void hideThrobberItem() {
        Basic_ListItem tailItem = getTailItem();

        if (tailItem instanceof Basic_ThrobberItem)
            removeItem(mItemsList.indexOf(tailItem));
    }


    @Override
    public void showLoadmoreItem() {
        addItem(new Basic_LoadmoreItem());
    }

    @Override
    public void showLoadmoreItem(int titleId) {
        addItem(new Basic_LoadmoreItem(titleId));
    }

    @Override
    public void hideLoadmoreItem() {
        Basic_ListItem tailItem = getTailItem();

        if (tailItem instanceof Basic_LoadmoreItem)
            removeItem(mItemsList.indexOf(tailItem));
    }


    // Фильтрация
    @Override
    public void prepareFilter() {
        if (null == mFilter) {
            saveOriginalItemsList();

            mFilter = new Basic_ItemsFilter(mOriginalItemsList, new Basic_ItemsFilter.CompleteCallback() {
                @Override
                public void onListFiltered(List<Basic_ListItem> filteredList) {
                    setList(filteredList);
                }
            });
        }
    }

    @Override
    public void removeFilter() {
        mFilter = null;
        mOriginalItemsList.clear();
        mFilterPattern = null;
    }

    @Override
    public String getFilterText() {
        return mFilterPattern;
    }

    @Override
    public void filterItems(String pattern) {
        // Обход срабатывания при обновлении списка
        if (null == mFilter)
            return;

        // Обход срабатывания при посстановлении после поворота экрана
        if (null != mFilterPattern && mFilterPattern.equals(pattern))
            return;

        // Не помню, что...
        if (null == pattern)
            throw new RuntimeException("Filter pattern == null (что за ситуация?)");
        else
            mFilterPattern = pattern;

        applyFilter(pattern);
    }

    @Override
    public boolean isFiltered() {
        return !TextUtils.isEmpty(mFilterPattern);
    }

    // Сортировка
    @Override
    public void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        iItemsComparator itemsComparator = getItemsComparator();

        if (null != itemsComparator) {
            itemsComparator.setSortingMode(sortingMode, sortingOrder);
            performSorting(mItemsList, itemsComparator);
            mIsSorted = true;
        }
    }

    @Override
    public boolean isSelectionMode() {
        return mSelectedItemsCount > 0;
    }


    // Filterable
    @Override
    public Filter getFilter() {
        return mFilter;
    }


    // Выбор элементов
    @Override
    public void toggleItemSelection(int position) {
        Basic_DataItem dataItem = (Basic_DataItem) getItem(position);

        if (dataItem.isSelected()) {
            dataItem.setSelected(false);
            decreaseSelectedItemsCount();
        }
        else {
            dataItem.setSelected(true);
            increaseSelectedItemsCount();
        }

        refreshItem(position);
    }

    @Override
    public Integer getSelectedItemsCount() {
        return mSelectedItemsCount;
    }

    @Override
    public void selectAll() {
        for (Basic_ListItem listItem : mItemsList) {
            if (listItem instanceof Basic_DataItem){
                Basic_DataItem dataItem = (Basic_DataItem) listItem;
                if (!dataItem.isSelected()) {
                    dataItem.setSelected(true);
                    increaseSelectedItemsCount();
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void clearSelection() {
        for (Basic_ListItem listItem : mItemsList)
            if (listItem instanceof Basic_DataItem)
                ((Basic_DataItem) listItem).setSelected(false);

        resetSelectionCounter();

        notifyDataSetChanged();
    }

    @Override
    public void invertSelection() {
        for (Basic_ListItem listItem : mItemsList)
        {
            if (listItem instanceof Basic_DataItem)
            {
                Basic_DataItem dataItem = (Basic_DataItem) listItem;

                if (dataItem.isSelected()) {
                    dataItem.setSelected(false);
                    decreaseSelectedItemsCount();
                }
                else {
                    dataItem.setSelected(true);
                    increaseSelectedItemsCount();
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public void resetSelectionCounter() {
        mSelectedItemsCount = 0;
    }


    // Внутренние
    private int getMaxIndex() {
        int listSize = mItemsList.size();
        return (0==listSize) ? -1 : listSize - 1;
    }

    private void replaceItem(int position, Basic_ListItem listItem) {
        if (position < 0)
            throw new RuntimeException("List position cannot be < 0");

        mItemsList.set(position, listItem);
        notifyItemChanged(position);
    }

    private void removeItem(int position) {
        if (position < 0)
            throw new RuntimeException("List position cannot be < 0");

        mItemsList.remove(position);
        notifyItemRemoved(position);
    }

    /*private void removeLastNonDataItem() {
        Basic_ListItem listItem = getTailItem();

        if (listItem instanceof Basic_DataItem)
            return;

        removeItem(listItem);
    }*/

    private Basic_ListItem getTailItem() {
        int maxIndex = getMaxIndex();

        if (-1 == getMaxIndex())
            return null;

        return mItemsList.get(maxIndex);
    }


    private void increaseSelectedItemsCount() {
        mSelectedItemsCount++;

    }

    private void decreaseSelectedItemsCount() {
        mSelectedItemsCount--;
        if (mSelectedItemsCount < 0)
            throw new RuntimeException("Selected items count becomes negative ("+ mSelectedItemsCount +")");
    }


    private void performSorting(List<Basic_ListItem> itemsList, iItemsComparator comparator) {
        Collections.sort(itemsList, comparator);
        notifyDataSetChanged();
    }


    private void saveOriginalItemsList() {
        mOriginalItemsList.clear();

        if (0 == mItemsList.size())
            return;

        int maxIndex = getMaxIndex();
        Basic_ListItem lastItem = mItemsList.get(maxIndex);

        if (lastItem instanceof Basic_LoadmoreItem)
            mItemsList.remove(maxIndex);

        if (lastItem instanceof Basic_ThrobberItem)
            mItemsList.remove(maxIndex);

        mOriginalItemsList.addAll(mItemsList);
    }

    private void addToOriginalItemsList(List<Basic_ListItem> list) {
        mOriginalItemsList.addAll(list);
    }

    private void applyFilter() {
        applyFilter(null);
    }

    private void applyFilter(@Nullable String pattern) {
        Filter filter = getFilter();

        filter.filter(mFilterPattern);
    }

}
