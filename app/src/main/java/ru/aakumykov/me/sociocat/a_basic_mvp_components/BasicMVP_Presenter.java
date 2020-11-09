package ru.aakumykov.me.sociocat.a_basic_mvp_components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BuildConfig;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasic_SortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicListPage;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSearchViewListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSelectionCommandsListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;


public abstract class BasicMVP_Presenter
        implements
        iBasicMVP_ItemClickListener,
        iSearchViewListener,
        iSelectionCommandsListener
{
    protected iBasicListPage mPageView;
    protected iBasicList mListView;

    protected iViewState mCurrentViewState;
    protected Object mCurrentViewStateData;

    protected iSortingMode mCurrentSortingMode = eBasic_SortingMode.BY_NAME;
    protected eSortingOrder mCurrentSortingOrder = eSortingOrder.DIRECT;


    public void bindViews(iBasicListPage pageView, iBasicList listView) {
        mPageView = pageView;
        mListView = listView;
    }

    public abstract void unbindViews();

    protected void onResume() {
        if (null != mCurrentViewState)
            setViewState(mCurrentViewState, mCurrentViewStateData);
    }

    protected void onStop() {
        unbindViews();
    }

    public  void onMenuCreated() {
        if (isColdStart())
            onColdStart();
        else
            onConfigChanged();
    }

    public void updateSelectionModeMenu() {

        int dataItemsCount = mListView.getDataItemsCount();
        int selectedItemsCount = mListView.getSelectedItemsCount();

        if (selectedItemsCount == dataItemsCount) {
            setViewState(eBasicViewStates.SELECTION_ALL, selectedItemsCount);
        }
        else if (0 == selectedItemsCount) {
            setViewState(eBasicViewStates.NEUTRAL, null);
        }
        else {
            setViewState(eBasicViewStates.SELECTION, selectedItemsCount);
        }
    }


    private boolean isColdStart() {
        return mListView.isVirgin();
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

    protected void setViewState(@NonNull iViewState newViewState, @Nullable Object data) {
        mCurrentViewState = newViewState;
        mCurrentViewStateData = data;
        mPageView.setViewState(newViewState, data);
    }

    protected void setErrorViewState(int userMessageId, String debugMessage) {
        setViewState(eBasicViewStates.ERROR, (BuildConfig.DEBUG) ? debugMessage : userMessageId);
    }


    @Override
    public abstract void onItemLongClicked(BasicMVP_DataViewHolder basicViewHolder);

    @Override
    public abstract void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder);

    public iSortingMode getCurrentSortingMode() {
        return mCurrentSortingMode;
    }

    public eSortingOrder getCurrentSortingOrder() {
        return mCurrentSortingOrder;
    }

    public void onSortMenuItemClicked(iSortingMode sortingMode) {
        mCurrentSortingMode = sortingMode;
        mCurrentSortingOrder = mCurrentSortingOrder.reverse();

        mListView.sortList(mCurrentSortingMode, mCurrentSortingOrder);

        mPageView.refreshMenu();
    }


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
        mListView.showLoadmoreItem();
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


}
