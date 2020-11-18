package ru.aakumykov.me.sociocat.b_basic_mvp_components2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BuildConfig;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSearchViewListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSelectionCommandsListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

public abstract class BasicMVP_Presenter
        implements
        iBasicMVP_ItemClickListener,
        iSearchViewListener,
        iSelectionCommandsListener
{
    protected iBasicList_Page mPageView;
    protected iBasicList mListView;

    protected iBasicViewState mCurrentViewState;
    protected Object mCurrentViewStateData;

    protected iSortingMode mCurrentSortingMode;
    protected eSortingOrder mCurrentSortingOrder;


    public BasicMVP_Presenter(iSortingMode defaultSortingMode) {
        mCurrentSortingMode = defaultSortingMode;
        mCurrentSortingOrder = getDefaultSortingOrderForSortingMode(mCurrentSortingMode);
    }

    public void bindViews(iBasicList_Page pageView, iBasicList listView) {
        mPageView = pageView;
        mListView = listView;
    }

    public abstract void unbindViews();

    public  void onMenuCreated() {
        if (isColdStart())
            onColdStart();
        else
            onConfigChanged();
    }

    public void updateSelectionModeMenu() {

        int visibleDataItemsCount = mListView.getVisibleDataItemsCount();
        int selectedItemsCount = mListView.getSelectedItemsCount();

        if (selectedItemsCount == visibleDataItemsCount) {
            setViewState(eBasicViewStates.SELECTION_ALL, selectedItemsCount);
        }
        else if (0 == selectedItemsCount) {
            setViewState(eBasicViewStates.NEUTRAL, null);
        }
        else {
            setViewState(eBasicViewStates.SELECTION, selectedItemsCount);
        }
    }

    public iSortingMode getCurrentSortingMode() {
        return mCurrentSortingMode;
    }

    public eSortingOrder getCurrentSortingOrder() {
        return mCurrentSortingOrder;
    }

    public void onSortMenuItemClicked(iSortingMode sortingMode) {

        mCurrentSortingOrder = getSortingOrderForSortingMode(sortingMode);

        // !Присваивание mCurrentSortingMode должно производиться после mCurrentSortingOrder!
        mCurrentSortingMode = sortingMode;

        mListView.sortList(mCurrentSortingMode, mCurrentSortingOrder);

        mPageView.refreshMenu();
    }


    protected void onResume() {
        if (null != mCurrentViewState)
            setViewState(mCurrentViewState, mCurrentViewStateData);
    }

    protected void onStop() {
        unbindViews();
    }

    public boolean onBackPressed() {
        if (mListView.isSelectionMode()) {
            mListView.clearSelection();
            setViewState(eBasicViewStates.NEUTRAL, null);
            return true;
        }
        return false;
    }

    protected abstract eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode);

    protected eSortingOrder getSortingOrderForSortingMode(iSortingMode sortingMode) {
        if (null != mCurrentSortingOrder && mCurrentSortingMode.equals(sortingMode))
            return mCurrentSortingOrder.reverse();
        else
            return getDefaultSortingOrderForSortingMode(sortingMode);
    }

    protected void onColdStart() {
        setViewState(eBasicViewStates.NEUTRAL, null);
//        mPageView.setDefaultPageTitle();
//        mPageView.compileMenu();
    }

    protected void onConfigChanged() {
        mPageView.setViewState(mCurrentViewState, mCurrentViewStateData);
    }

    protected abstract void onRefreshRequested();

    protected void setViewState(@NonNull iBasicViewState newViewState, @Nullable Object data) {
        mCurrentViewState = newViewState;
        mCurrentViewStateData = data;
        mPageView.setViewState(newViewState, data);
    }

    protected void setErrorViewState(int userMessageId, String debugMessage) {
        setViewState(eBasicViewStates.ERROR, (BuildConfig.DEBUG) ? debugMessage : userMessageId);
    }


    // iBasicMVP_ItemClickListener
    @Override
    public abstract void onItemClicked(BasicMVP_DataViewHolder basicDataViewHolder);

    @Override
    public abstract void onItemLongClicked(BasicMVP_DataViewHolder basicDataViewHolder);

    @Override
    public abstract void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder);


    // iSearchViewListener
    @Override
    public void onSearchViewCreated() {
        if (mListView.isFiltered())
            mPageView.restoreSearchView(mListView.getFilterText());
    }

    @Override
    public void onSearchViewOpened() {
        mListView.prepareFilter();
    }

    @Override
    public void onSearchViewClosed() {
        mListView.removeFilter();
    }

    @Override
    public void onSearchViewTextChanged(String pattern) {
        mListView.filterItems(pattern);
    }

    @Override
    public void onSearchViewTextSubmitted(String pattern) {

    }


    // iSelectionCommandsListener
    @Override
    public void onSelectItemClicked(BasicMVP_DataViewHolder basicDataViewHolder) {
        mListView.toggleItemSelection(basicDataViewHolder.getAdapterPosition());
        updateSelectionModeMenu();
    }

    @Override
    public void onSelectAllClicked() {
        mListView.selectAll();
        setViewState(eBasicViewStates.SELECTION_ALL, mListView.getSelectedItemsCount());
    }

    @Override
    public void onClearSelectionClicked() {
        mListView.clearSelection();
        setViewState(eBasicViewStates.NEUTRAL, null);
    }

    @Override
    public void onInvertSelectionClicked() {
        mListView.invertSelection();
        setViewState(eBasicViewStates.SELECTION, mListView.getSelectedItemsCount());
    }


    private boolean isColdStart() {
        return mListView.isVirgin();
    }


}
