package ru.aakumykov.me.sociocat.a_basic_mvp_list_components;


import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSearchViewListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSelectionCommandsListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.FeedViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.GridViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.AllItemsSelectedViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.ErrorViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.NeutralViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.NoneItemsSelectedViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.RefreshingViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.SomeItemsSelectedViewState;

public abstract class BasicMVPList_Presenter
        implements
        iBasicMVP_ItemClickListener,
        iSearchViewListener,
        iSelectionCommandsListener
{
    private static final String TAG = BasicMVPList_Presenter.class.getSimpleName();

    protected iBasicList_Page mPageView;
    protected iBasicList mListView;

    protected BasicViewMode mCurrentViewMode;
    protected iViewState mCurrentViewState;
    protected iSortingMode mCurrentSortingMode;
    protected eSortingOrder mCurrentSortingOrder;

    private boolean mInterruptFlag = false;


    public BasicMVPList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        mCurrentViewMode = defaultViewMode;
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
            setViewState(new AllItemsSelectedViewState(selectedItemsCount));
        }
        else if (0 == selectedItemsCount) {
            setViewState(new NoneItemsSelectedViewState());
        }
        else {
            setViewState(new SomeItemsSelectedViewState(selectedItemsCount));
        }
    }

    public BasicViewMode getCurrentViewMode() {
        return mCurrentViewMode;
    }

    public iSortingMode getCurrentSortingMode() {
        return mCurrentSortingMode;
    }

    public eSortingOrder getCurrentSortingOrder() {
        return mCurrentSortingOrder;
    }

    public void onSortMenuItemClicked(iSortingMode sortingMode) {

        // !!! Присваивание sortingOrder должно предшествовать присваиванию sortingMode !!!
        mCurrentSortingOrder = getSortingOrderForSortingMode(sortingMode);

        // !!! Присваивание sortingMode должно производиться после sortingOrder !!!
        mCurrentSortingMode = sortingMode;

        mListView.sortList(mCurrentSortingMode, mCurrentSortingOrder);

        mPageView.refreshMenu();
    }


    // Переключение режимов просмотра
    public void onViewModeListClicked() {
        changeLayoutTo(new ListViewMode());
    }

    public void onViewModeGridClicked() {
        changeLayoutTo(new GridViewMode());
    }

    public void onViewModeFeedClicked() {
        changeLayoutTo(new FeedViewMode());
    }

    protected void onResume() {
//        if (null != mCurrentViewState)
//            setViewState(mCurrentViewState);
    }

    protected void onStop() {
        unbindViews();
    }

    public boolean onBackPressed() {
        if (mListView.isSelectionMode()) {
            mListView.clearSelection();
            setViewState(new NeutralViewState());
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
        //setNeutralViewState(); // Не место! Дочерние компоненты сами решают, какой статус делать вначале.
    }

    protected void onConfigChanged() {
        if (null != mCurrentViewState)
            setViewState(mCurrentViewState);
    }

    protected abstract void onRefreshRequested();

    protected void setViewState(@NonNull iViewState viewState) {
        mCurrentViewState = viewState;
        mPageView.setViewState(viewState);
    }

    protected void runFromBackground(@NonNull Runnable runnable) {
        mPageView.runDelayed(runnable, 0);
    }

    protected void setNeutralViewState() {
        setViewState(new NeutralViewState());
    }

    protected void setRefreshingViewState() {
        setViewState(new RefreshingViewState());
    }

    protected void setErrorViewState(int userMessageId, String debugMessage) {
        mPageView.setViewState(new ErrorViewState(userMessageId, debugMessage));
    }

    protected void onInterruptRunningProcessClicked() {
        setInterruptFlag();
    }

    protected final void setInterruptFlag() {
        mInterruptFlag = true;
    }

    protected final void clearInterruptFlag() {
        mInterruptFlag = false;
    }

    protected boolean hasInterruptFlag() {
        return mInterruptFlag;
    }


    // iBasicMVP_ItemClickListener
    @Override
    public abstract void onItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder);

    @Override
    public abstract void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder);

    @Override
    public abstract void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder);


    // iSearchViewListener
    @Override
    public void onSearchViewCreated() {
        if (mListView.isFiltered())
            mPageView.restoreSearchView(mListView.getFilterText());
    }

    @Override
    public void onSearchViewOpened() {
        mListView.setTextFilter(getItemsTextFilter());
    }

    @Override
    public void onSearchViewTextChanged(String pattern) {
        mListView.filterList(pattern);
    }

    @Override
    public void onSearchViewTextSubmitted(String pattern) {

    }

    @Override
    public void onSearchViewClosed() {
        mListView.removeTextFilter();
    }


    protected abstract BasicMVPList_ItemsTextFilter getItemsTextFilter();


    // iSelectionCommandsListener
    @Override
    public void onSelectItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {
        mListView.toggleItemSelection(basicDataViewHolder.getAdapterPosition());
        updateSelectionModeMenu();
    }

    @Override
    public void onSelectAllClicked() {
        mListView.selectAll();
        updateSelectionModeMenu();
    }

    @Override
    public void onClearSelectionClicked() {
        mListView.clearSelection();
        updateSelectionModeMenu();
    }

    @Override
    public void onInvertSelectionClicked() {
        mListView.invertSelection();
        updateSelectionModeMenu();
    }


    private boolean isColdStart() {
        return mListView.isVirgin();
    }

    private void changeLayoutTo(BasicViewMode viewMode) {
        if (null != mCurrentViewMode && mCurrentViewMode.equals(viewMode))
            return;

        mCurrentViewMode = viewMode;
        mListView.setViewMode(viewMode);
        mPageView.reConfigureRecyclerView();
    }
}
