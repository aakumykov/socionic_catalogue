package ru.aakumykov.me.sociocat.b_basic_mvp_components2;


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

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_LoadmoreItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ThrobberItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_utils.BasicMVP_ItemsFilter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.ListViewMode;

public abstract class BasicMVP_DataAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements iBasicList
{
    private boolean mIsVirgin = true;
    private boolean mIsSorted = false;

    private final List<BasicMVP_ListItem> mCurrentItemsList;
    private final List<BasicMVP_ListItem> mOriginalItemsList;
    private final ArrayList<BasicMVP_DataItem> mSelectedItemsList;

    protected BasicMVP_ViewHolderCreator mViewHolderCreator;
    protected BasicMVP_ViewHolderBinder mViewHolderBinder;
    protected BasicMVP_ViewTypeDetector mViewTypeDetector;

    protected iBasicMVP_ItemClickListener mItemClickListener;

    private String mFilterPattern;
    private Filter mFilter;

    private BasicMVP_DataItem mCurrentHighlightedItem;
    private BasicViewMode mCurrentViewMode = new ListViewMode();


    public BasicMVP_DataAdapter(
            iBasicMVP_ItemClickListener itemClickListener
    )
    {
        mItemClickListener = itemClickListener;

        mCurrentItemsList = new ArrayList<>();
        mOriginalItemsList = new ArrayList<>();
        mSelectedItemsList = new ArrayList<>();

        mViewHolderCreator = prepareViewHolderCreator();
        mViewHolderBinder = prepareViewHolderBinder();
        mViewTypeDetector = prepareViewTypeDetector();
    }


    protected abstract BasicMVP_ViewHolderCreator prepareViewHolderCreator();
    protected abstract BasicMVP_ViewHolderBinder prepareViewHolderBinder();
    protected abstract BasicMVP_ViewTypeDetector prepareViewTypeDetector();
    protected abstract iItemsComparator getItemsComparator();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mViewHolderCreator.createViewHolder(parent, viewType, mCurrentViewMode);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BasicMVP_ListItem listItem = getItem(position);
        mViewHolderBinder.bindViewHolder(holder, position, listItem);
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypeDetector.getItemType(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mCurrentItemsList.size();
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
    public void highlightItem(int position) {
        int oldHighlightedPosition = mCurrentItemsList.indexOf(mCurrentHighlightedItem);

        BasicMVP_ListItem listItem = getItem(position);

        BasicMVP_DataItem newHighlightedItem = (listItem instanceof BasicMVP_DataItem) ?
            (BasicMVP_DataItem) listItem : null;

        if (null != newHighlightedItem)
            newHighlightedItem.setHighLighted(true);

        if (null != mCurrentHighlightedItem)
            mCurrentHighlightedItem.setHighLighted(false);

        mCurrentHighlightedItem = newHighlightedItem;

        refreshItem(oldHighlightedPosition);
        refreshItem(position);
    }

    @Override
    public void setViewMode(BasicViewMode viewMode) {
        mCurrentViewMode = viewMode;
    }

    @Override
    public void setList(List<BasicMVP_ListItem> inputList) {
        mIsVirgin = false;

        mCurrentItemsList.clear();
        mCurrentItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void setListAndFilter(List<BasicMVP_ListItem> inputList) {
        mIsVirgin = false;

        mOriginalItemsList.clear();
        mOriginalItemsList.addAll(inputList);
        applyFilter(mFilterPattern);

//        mItemsList.clear();
//        mItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<BasicMVP_ListItem> inputList) {
        appendListAndSort(inputList, null, null);
    }

    @Override
    public void appendListAndSort(List<BasicMVP_ListItem> inputList, @Nullable iSortingMode sortingMode, @Nullable eSortingOrder sortingOrder) {
        hideThrobberItem();
        hideLoadmoreItem();

        int insertPosition = getMaxIndex() + 1;
        mCurrentItemsList.addAll(inputList);

        if (isSorted())
            if (null != sortingMode && null != sortingOrder)
                sortList(sortingMode, sortingOrder);

        notifyItemRangeInserted(insertPosition, inputList.size());
    }

    @Override
    public void appendListAndFilter(List<BasicMVP_ListItem> inputList) {
        addToOriginalItemsList(inputList);
        applyFilter();
    }

    @Override
    public void addItem(BasicMVP_ListItem listItem) {
        mCurrentItemsList.add(listItem);
        notifyItemInserted(getMaxIndex());
    }

    @Override
    public void removeItem(BasicMVP_ListItem item) {
        mOriginalItemsList.remove(item);

        if (item instanceof BasicMVP_DataItem)
            mSelectedItemsList.remove(item);

        int itemsListIndex = mCurrentItemsList.indexOf(item);
        mCurrentItemsList.remove(itemsListIndex);
        notifyItemRemoved(itemsListIndex);
    }

    @Override
    public void updateItemInVisibleList(int position, BasicMVP_ListItem item) {
        if (positionIsInListRange(position, mCurrentItemsList)) {
            mCurrentItemsList.set(position, item);
            notifyItemChanged(position);
        }
    }

    @Override
    public void updateItemInOriginalList(int position, BasicMVP_ListItem item) {
        if (positionIsInListRange(position, mOriginalItemsList))
            mOriginalItemsList.set(position, item);
    }

    @Override
    public int getVisibleItemsCount() {
        return mCurrentItemsList.size();
    }

    @Override
    public int findVisibleObjectPosition(iComparisionCallback callback) {
        for (BasicMVP_ListItem listItem : mCurrentItemsList)
        {
            if (listItem instanceof BasicMVP_DataItem)
            {
                Object objectFromList = ((BasicMVP_DataItem) listItem).getPayload();
                if (callback.onCompare(objectFromList))
                    return mCurrentItemsList.indexOf(listItem);
            }
        }
        return -1;
    }

    @Override
    public int findOriginalObjectPosition(iComparisionCallback callback) {
        for (BasicMVP_ListItem listItem : mOriginalItemsList)
        {
            if (listItem instanceof BasicMVP_DataItem)
            {
                Object objectFromList = ((BasicMVP_DataItem) listItem).getPayload();
                if (callback.onCompare(objectFromList))
                    return mOriginalItemsList.indexOf(listItem);
            }
        }
        return -1;
    }

    @Override
    public int getVisibleDataItemsCount() {
        return getVisibleDataItems().size();
    }

    @Override
    public int getOriginalDataItemsCount() {
        return getOriginalDataItems().size();
    }

    @Override
    public List<BasicMVP_DataItem> getVisibleDataItems() {

        List<BasicMVP_DataItem> dataItems = new ArrayList<>();

        /*ListIterator<BasicMVP_ListItem> listIterator =
                mCurrentItemsList.listIterator(getVisibleItemsCount());

        while (listIterator.hasNext()) {
            BasicMVP_ListItem listItem = listIterator.next();

            if (listItem instanceof BasicMVP_DataItem)
                dataItems.add((BasicMVP_DataItem) listItem);
        }*/

        for (BasicMVP_ListItem listItem : mCurrentItemsList)
            if (listItem instanceof BasicMVP_DataItem)
                dataItems.add((BasicMVP_DataItem) listItem);

        return dataItems;
    }

    @Override
    public BasicMVP_ListItem getItem(int position) {
        if (position >=0 && mCurrentItemsList.size() > position)
            return mCurrentItemsList.get(position);
        else
            return null;
    }

    @Override
    public BasicMVP_DataItem getLastDataItem() {
        ListIterator<BasicMVP_ListItem> listIterator = mCurrentItemsList.listIterator(mCurrentItemsList.size());
        BasicMVP_ListItem listItem;
        while (listIterator.hasPrevious()) {
            listItem = listIterator.previous();
            if (listItem instanceof BasicMVP_DataItem)
                return (BasicMVP_DataItem) listItem;
        }
        return null;
    }

    @Override
    public void refreshItem(int position) {
        notifyItemChanged(position);
    }


    @Override
    public void showThrobberItem() {
        addItem(new BasicMVP_ThrobberItem());
    }

    @Override
    public void hideThrobberItem() {
        BasicMVP_ListItem tailItem = getTailItem();

        if (tailItem instanceof BasicMVP_ThrobberItem)
            removeItem(mCurrentItemsList.indexOf(tailItem));
    }


    @Override
    public void showLoadmoreItem() {
        addItem(new BasicMVP_LoadmoreItem());
    }

    @Override
    public void showLoadmoreItem(int titleId) {
        addItem(new BasicMVP_LoadmoreItem(titleId));
    }

    @Override
    public void hideLoadmoreItem() {
        BasicMVP_ListItem tailItem = getTailItem();

        if (tailItem instanceof BasicMVP_LoadmoreItem)
            removeItem(mCurrentItemsList.indexOf(tailItem));
    }


    // Фильтрация
    @Override
    public void prepareFilter() {
        if (null == mFilter) {
            saveOriginalItemsList();

            mFilter = new BasicMVP_ItemsFilter(mOriginalItemsList, new BasicMVP_ItemsFilter.CompleteCallback() {
                @Override
                public void onListFiltered(List<BasicMVP_ListItem> filteredList) {
                    setList(filteredList);
                    showLoadmoreItem();
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
            performSorting(mCurrentItemsList, itemsComparator);
            mIsSorted = true;
        }
    }

    @Override
    public boolean isSelectionMode() {
        return mSelectedItemsList.size() > 0;
    }


    // Filterable
    @Override
    public Filter getFilter() {
        return mFilter;
    }


    // Выбор элементов
    @Override
    public void toggleItemSelection(int position) {

        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) getItem(position);

        if (!dataItem.isSelected()) {
            dataItem.setSelected(true);
            addToSelectedItemsList(dataItem);
        }
        else {
            dataItem.setSelected(false);
            removeFromSelectedItemsList(dataItem);
        }

        refreshItem(position);
    }

    @Override
    public Integer getSelectedItemsCount() {
        return mSelectedItemsList.size();
    }

    @Override
    public List<BasicMVP_DataItem> getSelectedItems() {
        return new ArrayList<>(mSelectedItemsList);
    }

    @Override
    public void selectAll() {
        for (BasicMVP_ListItem listItem : mCurrentItemsList) {
            if (listItem instanceof BasicMVP_DataItem){
                BasicMVP_DataItem dataItem = (BasicMVP_DataItem) listItem;
                if (!dataItem.isSelected()) {
                    dataItem.setSelected(true);
                    addToSelectedItemsList(dataItem);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void clearSelection() {
        for (BasicMVP_ListItem listItem : mCurrentItemsList)
            if (listItem instanceof BasicMVP_DataItem)
                ((BasicMVP_DataItem) listItem).setSelected(false);

        mSelectedItemsList.clear();

        notifyDataSetChanged();
    }

    @Override
    public void invertSelection() {
        for (BasicMVP_ListItem listItem : mCurrentItemsList)
        {
            if (listItem instanceof BasicMVP_DataItem)
            {
                BasicMVP_DataItem dataItem = (BasicMVP_DataItem) listItem;

                if (dataItem.isSelected()) {
                    dataItem.setSelected(false);
                    removeFromSelectedItemsList(dataItem);
                }
                else {
                    dataItem.setSelected(true);
                    addToSelectedItemsList(dataItem);
                }
            }
        }

        notifyDataSetChanged();
    }


    // Внутренние
    private int getMaxIndex() {
        int listSize = mCurrentItemsList.size();
        return (0==listSize) ? -1 : listSize - 1;
    }

    private void replaceItem(int position, BasicMVP_ListItem listItem) {
        if (position < 0)
            throw new RuntimeException("List position cannot be < 0");

        mCurrentItemsList.set(position, listItem);
        notifyItemChanged(position);
    }

    private void removeItem(int position) {
        if (position < 0)
            throw new RuntimeException("List position cannot be < 0");

        mCurrentItemsList.remove(position);
        notifyItemRemoved(position);
    }

    /*private void removeLastNonDataItem() {
        BasicMVP_ListItem listItem = getTailItem();

        if (listItem instanceof BasicMVP_DataItem)
            return;

        removeItem(listItem);
    }*/

    private BasicMVP_ListItem getTailItem() {
        int maxIndex = getMaxIndex();

        if (-1 == getMaxIndex())
            return null;

        return mCurrentItemsList.get(maxIndex);
    }

    private void performSorting(List<BasicMVP_ListItem> itemsList, iItemsComparator comparator) {
        Collections.sort(itemsList, comparator);
        notifyDataSetChanged();
    }

    private void saveOriginalItemsList() {

        if (0 == mCurrentItemsList.size())
            return;

        mOriginalItemsList.clear();

        mOriginalItemsList.addAll(getVisibleDataItems());
    }

    private void addToOriginalItemsList(List<BasicMVP_ListItem> list) {
        mOriginalItemsList.addAll(list);
    }

    private void applyFilter() {
        applyFilter(null);
    }

    private void applyFilter(@Nullable String pattern) {
        Filter filter = getFilter();

        filter.filter(mFilterPattern);
    }

    private void addToSelectedItemsList(BasicMVP_DataItem dataItem) {
        if (!mSelectedItemsList.contains(dataItem))
            mSelectedItemsList.add(dataItem);
    }

    private void removeFromSelectedItemsList(BasicMVP_DataItem dataItem) {
        mSelectedItemsList.remove(dataItem);
    }

    private List<BasicMVP_DataItem> getOriginalDataItems() {

        List<BasicMVP_DataItem> dataItems = new ArrayList<>();

        for (BasicMVP_ListItem listItem : mOriginalItemsList)
            if (listItem instanceof BasicMVP_DataItem)
                dataItems.add((BasicMVP_DataItem) listItem);

        return dataItems;
    }

    private boolean positionIsInListRange(int position, List<BasicMVP_ListItem> list) {
        return (position >= 0) && (list.size() > position);
    }
}
