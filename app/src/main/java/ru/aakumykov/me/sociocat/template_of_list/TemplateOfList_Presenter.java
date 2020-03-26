package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.template_of_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.stubs.TemplateOfList_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.template_of_list.stubs.TemplateOfList_ViewStub;
import ru.aakumykov.me.sociocat.utils.DeleteCard_Helper;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;

public class TemplateOfList_Presenter implements iTemplateOfList.iPresenter {

    private static final String TAG = TemplateOfList_Presenter.class.getSimpleName();
    private iTemplateOfList.iPageView pageView;
    private iTemplateOfList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iTemplateOfList.ViewState currentViewState;
    private Integer currentViewMessageId;
    private Object currentViewMessageDetails;
    private iTemplateOfList.LayoutMode currentLayoutMode;


    // iTemplateOfList.iPresenter
    @Override
    public void linkViewAndAdapter(iTemplateOfList.iPageView pageView, iTemplateOfList.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unlinkViewAndAdapter() {
        this.pageView = new TemplateOfList_ViewStub();
        this.dataAdapter = new TemplateOfList_DataAdapter_Stub();
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
    public iTemplateOfList.LayoutMode getCurrentLayoutMode() {
        return currentLayoutMode;
    }

    @Override
    public void storeViewState(iTemplateOfList.ViewState viewState, Integer messageId, Object messageDetails) {
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

        pageView.setViewState(iTemplateOfList.ViewState.REFRESHING, null, null);

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
        if (canStartSelection()) {
            pageView.setViewState(iTemplateOfList.ViewState.SELECTION, null, null);
            toggleItemSelection(dataItem);
        }
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
    public boolean canStartSelection() {
        return true;
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canEditSelectedItem() {
        return dataAdapter.isSingleItemSelected();
    }

    @Override
    public boolean canDeleteSelectedItem() {
        return true;
    }

    @Override
    public void onSelectAllClicked() {
        dataAdapter.selectAll(dataAdapter.getListSize());
        pageView.setViewState(iTemplateOfList.ViewState.SELECTION, null, dataAdapter.getSelectedItemsCount());
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

        MyDialogs.deleteSelectedCardsDialog(
                pageView.getActivity(),
                R.plurals.LIST_TEMPLATE_delete_selected_items_dialog_title,
                dataAdapter.getSelectedItemsCount(),
                new iMyDialogs.DeleteCallbacks() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        onDeleteSelectedCardsConfirmed();
                    }
                }
        );
    }

    @Override
    public void onActionModeDestroyed() {
        dataAdapter.clearSelection();
        showSuccessState();
    }

    @Override
    public void onChangeLayoutClicked() {
        if (null == currentLayoutMode || iTemplateOfList.LayoutMode.GRID.equals(currentLayoutMode))
            currentLayoutMode = iTemplateOfList.LayoutMode.LIST;
        else
            currentLayoutMode = iTemplateOfList.LayoutMode.GRID;

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
            String text = MyUtils.getStringWithString(
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

        int selectedItemsCount = dataAdapter.getSelectedItemsCount();

        if (0 == selectedItemsCount) {
            showSuccessState();
        } else {
            pageView.setViewState(iTemplateOfList.ViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void showSuccessState() {
        pageView.setViewState(iTemplateOfList.ViewState.SUCCESS, null, null);
    }

    private void showErrorState(int messageId, String errorMessage) {
        pageView.setViewState(iTemplateOfList.ViewState.ERROR, messageId, errorMessage);
    }

    private void onDeleteSelectedCardsConfirmed() {
        List<DataItem> selectedItems = dataAdapter.getSelectedItems();

        for (DataItem dataItem : selectedItems)
            deleteCard(dataItem);

        pageView.finishActionMode();
    }

    private void deleteCard(@NonNull DataItem dataItem) {
        dataAdapter.removeItem(dataItem);
    }

}
