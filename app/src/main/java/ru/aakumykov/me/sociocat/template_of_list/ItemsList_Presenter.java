package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.template_of_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.stubs.ItemsList_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.template_of_list.stubs.ItemsList_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ItemsList_Presenter implements iItemsList.iPresenter {

    private iItemsList.iPageView pageView;
    private iItemsList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iItemsList.ViewState currentViewState;
    private Integer currentViewMessageId;
    private Object currentViewMessageDetails;
    private iItemsList.LayoutMode currentLayoutMode;


    // iItemsList.iPresenter
    @Override
    public void linkViewAndAdapter(iItemsList.iPageView pageView, iItemsList.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unlinkViewAndAdapter() {
        this.pageView = new ItemsList_ViewStub();
        this.dataAdapter = new ItemsList_DataAdapter_Stub();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (null == intent) {
            showErrorState(R.string.data_error, "Intent is null");
            return;
        }

        pageView.changeLayout(currentLayoutMode);

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        updatePageTitle();

        pageView.changeLayout(currentLayoutMode);

        pageView.setViewState(currentViewState, currentViewMessageId, currentViewMessageDetails);
    }

    @Override
    public iItemsList.LayoutMode getCurrentLayoutMode() {
        return currentLayoutMode;
    }

    @Override
    public void storeViewState(iItemsList.ViewState viewState, Integer messageId, Object messageDetails) {
        this.currentViewState = viewState;
        this.currentViewMessageId = messageId;
        this.currentViewMessageDetails = messageDetails;
    }

    @Override
    public void onRefreshRequested() {

        DataItem lastDataItem = dataAdapter.getLastDataItem();

        if (null == lastDataItem) {
            showSuccessState();
            return;
        }

        pageView.setViewState(iItemsList.ViewState.REFRESHING, null, null);

        loadList();
    }

    @Override
    public void onDataItemClicked(DataItem dataItem) {
        if (pageView.actionModeIsActive())
            toggleItemSelection(dataItem);
        else
            pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onDataItemLongClicked(DataItem dataItem) {
        pageView.setViewState(iItemsList.ViewState.SELECTION, null, null);
        toggleItemSelection(dataItem);
    }

    @Override
    public void onLoadMoreClicked() {
        int scrollPosition = dataAdapter.getListSize() + 1;

        DataItem lastDataItem = dataAdapter.getLastDataItem();

        if (null != lastDataItem) {
            Object object = lastDataItem.getPayload();
            loadMoreCards(object);
        }
        else {
            loadList();
        }
    }

    @Override
    public void onListFiltered(CharSequence filterText, List<DataItem> filteredList) {
        dataAdapter.setList(filteredList);
        this.filterText = filterText;
    }

    @Override
    public boolean hasFilterText() {
        return !TextUtils.isEmpty(filterText);
    }

    @Override
    public CharSequence getFilterText() {
        return filterText;
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canEditSelectedItem() {
        Integer index = dataAdapter.getSingleSelectedItemIndex();

        return null != index;
    }

    @Override
    public boolean canDeleteSelectedItem() {
        return true;
    }

    @Override
    public void onSelectAllClicked() {
        dataAdapter.selectAll(dataAdapter.getListSize());
        pageView.setViewState(iItemsList.ViewState.SELECTION, null, dataAdapter.getSelectedItemCount());
    }

    @Override
    public void onClearSelectionClicked() {
        dataAdapter.clearSelection();
        showSuccessState();
    }

    @Override
    public void onEditSelectedItemClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onDeleteSelectedItemsClicked() {
        List<DataItem> selectedItems = dataAdapter.getSelectedItems();

        for (DataItem item : selectedItems)
            dataAdapter.removeItem(item);

        showSuccessState();
    }

    @Override
    public void onActionModeDestroyed() {
        dataAdapter.clearSelection();
        showSuccessState();
    }

    @Override
    public void onChangeLayoutClicked() {
        if (null == currentLayoutMode || iItemsList.LayoutMode.GRID.equals(currentLayoutMode))
            currentLayoutMode = iItemsList.LayoutMode.LIST;
        else
            currentLayoutMode = iItemsList.LayoutMode.GRID;

        pageView.changeLayout(currentLayoutMode);
        pageView.refreshMenu();
    }


    // Внутренние методы
    private interface iLoadListCallbacks {
        void onListLoaded(List<DataItem> list);
    }

    private void loadList() {
        dataAdapter.showThrobberItem();

        getRandomList(new iLoadListCallbacks() {
            @Override
            public void onListLoaded(List<DataItem> list) {
                showSuccessState();
                dataAdapter.hideThrobberItem();
                dataAdapter.setList(list);
                dataAdapter.showLoadmoreItem();
            }
        });
    }

    private void loadMoreCards(Object startingFromObject) {
        dataAdapter.showThrobberItem();

        getRandomList(new iLoadListCallbacks() {
            @Override
            public void onListLoaded(List<DataItem> list) {
                dataAdapter.hideThrobberItem();
                dataAdapter.appendList(list);
                dataAdapter.showLoadmoreItem();
            }
        });
    }

    private <T> List<DataItem> incapsulateObjects2DataItems(List<T> objectList) {
        List<DataItem> outputList = new ArrayList<>();
        for (Object object : objectList) {
            DataItem dataItem = new DataItem<Card>();
            dataItem.setPayload(object);
            outputList.add(dataItem);
        }
        return outputList;
    }

    private void updatePageTitle() {
        int count = dataAdapter.getListSize();
        pageView.setPageTitle(R.string.LIST_TEMPLATE_title_extended, String.valueOf(count));
    }

    private void getRandomList(iLoadListCallbacks callbacks) {
        callbacks.onListLoaded(createRandomList());
    }

    private List<DataItem> createRandomList() {
        int min = 1;
        int max = 100;
        int randomSize = MyUtils.random(1, 10);

        List<DataItem> list = new ArrayList<>();

        for (int i=1; i<=randomSize; i++) {
            String text = MyUtils.getString(
                    pageView.getAppContext(),
                    R.string.LIST_TEMPLATE_item_name,
                    String.valueOf(MyUtils.random(min, max))
            );
            list.add(new DataItem(text, MyUtils.random(min, max)));
        }

        return list;
    }

    private void toggleItemSelection(DataItem dataItem) {
        dataAdapter.toggleSelection(dataAdapter.getPositionOf(dataItem));

        int selectedItemsCount = dataAdapter.getSelectedItemCount();

        if (0 == selectedItemsCount) {
            showSuccessState();
        } else {
            pageView.setViewState(iItemsList.ViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void showSuccessState() {
        pageView.setViewState(iItemsList.ViewState.SUCCESS, null, null);
    }

    private void showErrorState(int messageId, String errorMessage) {
        pageView.setViewState(iItemsList.ViewState.ERROR, messageId, errorMessage);
    }
}
