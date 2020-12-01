package ru.aakumykov.me.sociocat.a_basic_mvp_list_components;


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

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_LoadmoreItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ThrobberItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public abstract class BasicMVPList_DataAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements iBasicList
{
    private boolean mIsVirgin = true;
    private boolean mIsSorted = false;

    private final List<BasicMVPList_ListItem> mCurrentItemsList;
    private final List<BasicMVPList_ListItem> mOriginalItemsList;
    private final ArrayList<BasicMVPList_DataItem> mSelectedItemsList;

    protected BasicMVPList_ViewHolderCreator mViewHolderCreator;
    protected BasicMVPList_ViewHolderBinder mViewHolderBinder;
    protected BasicMVPList_ViewTypeDetector mViewTypeDetector;

    protected iBasicMVP_ItemClickListener mItemClickListener;

    private String mFilterPattern;
    private Filter mFilter;

    private BasicMVPList_DataItem mCurrentHighlightedItem;
    private BasicViewMode mCurrentViewMode;


    public BasicMVPList_DataAdapter(
            BasicViewMode defaultViewMode,
            iBasicMVP_ItemClickListener itemClickListener
    )
    {
        mCurrentViewMode = defaultViewMode;
        mItemClickListener = itemClickListener;

        mCurrentItemsList = new ArrayList<>();
        mOriginalItemsList = new ArrayList<>();
        mSelectedItemsList = new ArrayList<>();

        mViewHolderCreator = prepareViewHolderCreator();
        mViewHolderBinder = prepareViewHolderBinder();
        mViewTypeDetector = prepareViewTypeDetector();
    }


    protected abstract BasicMVPList_ViewHolderCreator prepareViewHolderCreator();
    protected abstract BasicMVPList_ViewHolderBinder prepareViewHolderBinder();
    protected abstract BasicMVPList_ViewTypeDetector prepareViewTypeDetector();
    protected abstract iItemsComparator getItemsComparator();


    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mViewHolderCreator.createViewHolder(parent, viewType, mCurrentViewMode);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BasicMVPList_ListItem listItem = getItem(position);
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

        BasicMVPList_ListItem listItem = getItem(position);

        BasicMVPList_DataItem newHighlightedItem = (listItem instanceof BasicMVPList_DataItem) ?
            (BasicMVPList_DataItem) listItem : null;

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
        notifyDataSetChanged();
    }

    @Override
    public void setList(List<BasicMVPList_ListItem> inputList) {
        mIsVirgin = false;

        mCurrentItemsList.clear();
        mCurrentItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void setListAndFilter(List<BasicMVPList_ListItem> inputList) {
        mIsVirgin = false;

        mOriginalItemsList.clear();
        mOriginalItemsList.addAll(inputList);
        applyFilter(mFilterPattern);

//        mItemsList.clear();
//        mItemsList.addAll(inputList);
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<BasicMVPList_ListItem> inputList) {
        appendListAndSort(inputList, null, null);
    }

    @Override
    public void appendListAndSort(List<BasicMVPList_ListItem> inputList, @Nullable iSortingMode sortingMode, @Nullable eSortingOrder sortingOrder) {
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
    public void appendListAndFilter(List<BasicMVPList_ListItem> inputList) {
        addToOriginalItemsList(inputList);
        applyFilter();
    }

    @Override
    public void addItem(BasicMVPList_ListItem listItem) {
        mCurrentItemsList.add(listItem);
        notifyItemInserted(getMaxIndex());
    }

    @Override
    public void insertItem(int position, BasicMVPList_ListItem item) {
        mOriginalItemsList.add(position, item);
        mCurrentItemsList.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public void removeItem(BasicMVPList_ListItem item) {
        mOriginalItemsList.remove(item);

        if (item instanceof BasicMVPList_DataItem)
            mSelectedItemsList.remove(item);

        int itemsListIndex = mCurrentItemsList.indexOf(item);
        mCurrentItemsList.remove(itemsListIndex);
        notifyItemRemoved(itemsListIndex);
    }

    @Override
    public int getVisibleItemsCount() {
        return mCurrentItemsList.size();
    }

    @Override
    public int findVisibleObjectPosition(iFindItemComparisionCallback callback) {

        for (BasicMVPList_ListItem listItem : mCurrentItemsList)
        {
            if (listItem instanceof BasicMVPList_DataItem)
            {
                Object objectFromList = ((BasicMVPList_DataItem) listItem).getPayload();

                if (callback.onCompareWithListItemPayload(objectFromList))
                    return mCurrentItemsList.indexOf(listItem);
            }
        }

        return -1;
    }

    @Override
    public int findOriginalObjectPosition(iFindItemComparisionCallback callback) {
        for (BasicMVPList_ListItem listItem : mOriginalItemsList)
        {
            if (listItem instanceof BasicMVPList_DataItem)
            {
                Object objectFromList = ((BasicMVPList_DataItem) listItem).getPayload();
                if (callback.onCompareWithListItemPayload(objectFromList))
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
    public List<BasicMVPList_DataItem> getVisibleDataItems() {

        List<BasicMVPList_DataItem> dataItems = new ArrayList<>();

        /*ListIterator<BasicMVPList_ListItem> listIterator =
                mCurrentItemsList.listIterator(getVisibleItemsCount());

        while (listIterator.hasNext()) {
            BasicMVPList_ListItem listItem = listIterator.next();

            if (listItem instanceof BasicMVPList_DataItem)
                dataItems.add((BasicMVPList_DataItem) listItem);
        }*/

        for (BasicMVPList_ListItem listItem : mCurrentItemsList)
            if (listItem instanceof BasicMVPList_DataItem)
                dataItems.add((BasicMVPList_DataItem) listItem);

        return dataItems;
    }

    @Override
    public BasicMVPList_ListItem getItem(int position) {
        if (position >=0 && mCurrentItemsList.size() > position)
            return mCurrentItemsList.get(position);
        else
            return null;
    }

    @Override
    public BasicMVPList_DataItem getLastDataItem() {

        ListIterator<BasicMVPList_ListItem> listIterator =
                mCurrentItemsList.listIterator(mCurrentItemsList.size());

        BasicMVPList_ListItem listItem;

        while (listIterator.hasPrevious()) {
            listItem = listIterator.previous();
            if (listItem instanceof BasicMVPList_DataItem)
                return (BasicMVPList_DataItem) listItem;
        }

        return null;
    }

    @Override
    public BasicMVPList_DataItem getLastUnfilteredDataItem() {

        ListIterator<BasicMVPList_ListItem> listIterator =
                (0 == mOriginalItemsList.size()) ?
                        mCurrentItemsList.listIterator(mCurrentItemsList.size()) :
                        mOriginalItemsList.listIterator(mOriginalItemsList.size());

        BasicMVPList_ListItem listItem;

        while (listIterator.hasPrevious()) {
            listItem = listIterator.previous();
            if (listItem instanceof BasicMVPList_DataItem)
                return (BasicMVPList_DataItem) listItem;
        }

        return null;
    }

    @Override
    public void refreshItem(int position) {
        notifyItemChanged(position);
    }

    @Override
    public int updateItemInList(@NonNull BasicMVPList_DataItem newListItem,
                                @NonNull iFindItemComparisionCallback comparisionCallback)
    {
        int visiblePosition = findVisibleObjectPosition(comparisionCallback);
        updateItemInVisibleList(visiblePosition, newListItem);

        int originalPosition = findOriginalObjectPosition(comparisionCallback);
        updateItemInOriginalList(originalPosition, newListItem);

        return visiblePosition;
    }

    @Override
    public void deleteItemFromList(@NonNull iFindItemComparisionCallback comparisionCallback)
    {
        int visiblePosition = findVisibleObjectPosition(comparisionCallback);
        deleteItemFromVisibleList(visiblePosition);

        int originalPosition = findOriginalObjectPosition(comparisionCallback);
        deleteItemFromOriginalList(originalPosition);
    }

    @Override
    public void showThrobberItem() {
        addItem(new BasicMVPList_ThrobberItem());
    }

    @Override
    public void hideThrobberItem() {
        BasicMVPList_ListItem tailItem = getTailItem();

        if (tailItem instanceof BasicMVPList_ThrobberItem)
            removeItem(mCurrentItemsList.indexOf(tailItem));
    }


    @Override
    public void showLoadmoreItem() {
        addItem(new BasicMVPList_LoadmoreItem());
    }

    @Override
    public void showLoadmoreItem(int titleId) {
        addItem(new BasicMVPList_LoadmoreItem(titleId));
    }

    @Override
    public void hideLoadmoreItem() {
        BasicMVPList_ListItem tailItem = getTailItem();

        if (tailItem instanceof BasicMVPList_LoadmoreItem)
            removeItem(mCurrentItemsList.indexOf(tailItem));
    }


    // Фильтрация
    @Override
    public void prepareFilter() {
        if (null == mFilter) {
            saveOriginalItemsList();

            mFilter = new BasicMVPList_ItemsFilter(mOriginalItemsList, new BasicMVPList_ItemsFilter.CompleteCallback() {
                @Override
                public void onListFiltered(List<BasicMVPList_ListItem> filteredList) {
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

        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) getItem(position);

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
    public List<BasicMVPList_DataItem> getSelectedItems() {
        return new ArrayList<>(mSelectedItemsList);
    }

    @Override
    public void selectAll() {
        for (BasicMVPList_ListItem listItem : mCurrentItemsList) {
            if (listItem instanceof BasicMVPList_DataItem){
                BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;
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
        for (BasicMVPList_ListItem listItem : mCurrentItemsList)
            if (listItem instanceof BasicMVPList_DataItem)
                ((BasicMVPList_DataItem) listItem).setSelected(false);

        mSelectedItemsList.clear();

        notifyDataSetChanged();
    }

    @Override
    public void invertSelection() {
        for (BasicMVPList_ListItem listItem : mCurrentItemsList)
        {
            if (listItem instanceof BasicMVPList_DataItem)
            {
                BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;

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

    private void replaceItem(int position, BasicMVPList_ListItem listItem) {
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
        BasicMVPList_ListItem listItem = getTailItem();

        if (listItem instanceof BasicMVPList_DataItem)
            return;

        removeItem(listItem);
    }*/

    private BasicMVPList_ListItem getTailItem() {
        int maxIndex = getMaxIndex();

        if (-1 == getMaxIndex())
            return null;

        return mCurrentItemsList.get(maxIndex);
    }

    private void performSorting(List<BasicMVPList_ListItem> itemsList, iItemsComparator comparator) {
        Collections.sort(itemsList, comparator);
        notifyDataSetChanged();
    }

    private void saveOriginalItemsList() {

        if (0 == mCurrentItemsList.size())
            return;

        mOriginalItemsList.clear();

        mOriginalItemsList.addAll(getVisibleDataItems());
    }

    private void addToOriginalItemsList(List<BasicMVPList_ListItem> list) {
        mOriginalItemsList.addAll(list);
    }

    private void applyFilter() {
        applyFilter(null);
    }

    private void applyFilter(@Nullable String pattern) {
        Filter filter = getFilter();

        filter.filter(mFilterPattern);
    }

    private void addToSelectedItemsList(BasicMVPList_DataItem dataItem) {
        if (!mSelectedItemsList.contains(dataItem))
            mSelectedItemsList.add(dataItem);
    }

    private void removeFromSelectedItemsList(BasicMVPList_DataItem dataItem) {
        mSelectedItemsList.remove(dataItem);
    }

    private List<BasicMVPList_DataItem> getOriginalDataItems() {

        List<BasicMVPList_DataItem> dataItems = new ArrayList<>();

        for (BasicMVPList_ListItem listItem : mOriginalItemsList)
            if (listItem instanceof BasicMVPList_DataItem)
                dataItems.add((BasicMVPList_DataItem) listItem);

        return dataItems;
    }

    private boolean positionIsInListRange(int position, List<BasicMVPList_ListItem> list) {
        return (position >= 0) && (list.size() > position);
    }

    private void updateItemInVisibleList(int position, BasicMVPList_ListItem item) {
        if (positionIsInListRange(position, mCurrentItemsList)) {
            mCurrentItemsList.set(position, item);
            notifyItemChanged(position);
        }
    }

    private void updateItemInOriginalList(int position, BasicMVPList_ListItem item) {
        if (positionIsInListRange(position, mOriginalItemsList))
            mOriginalItemsList.set(position, item);
    }

    private void deleteItemFromVisibleList(int position) {
        if (positionIsInListRange(position, mCurrentItemsList)) {
            mCurrentItemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void deleteItemFromOriginalList(int position) {
        if (positionIsInListRange(position, mOriginalItemsList))
            mOriginalItemsList.remove(position);
    }
}