package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;
import ru.aakumykov.me.sociocat.template_of_list.stubs.ItemsList_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.template_of_list.stubs.ItemsList_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ItemsList_Presenter implements iItemsList.iPresenter {

    private iItemsList.iPageView pageView;
    private iItemsList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iItemsList.ViewState viewState;
    private Integer viewMessageId;
    private Object viewMessageDetails;
    private iItemsList.LayoutType currentLayoutType;


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
            pageView.setState(iItemsList.ViewState.ERROR, R.string.data_error, "Intent is null");
            return;
        }

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        updatePageTitle();

        pageView.setState(viewState, viewMessageId, viewMessageDetails);

        pageView.setLayoutType(currentLayoutType);
    }

    @Override
    public void storeViewState(iItemsList.ViewState viewState, Integer messageId, Object messageDetails) {
        this.viewState = viewState;
        this.viewMessageId = messageId;
        this.viewMessageDetails = messageDetails;
    }

    @Override
    public void storeLayoutType(iItemsList.LayoutType layoutType) {
        currentLayoutType = layoutType;
    }

    @Override
    public iItemsList.LayoutType getLayoutType() {
        return currentLayoutType;
    }

    @Override
    public void onRefreshRequested() {
        pageView.setState(iItemsList.ViewState.REFRESHING, null, null);
        loadList();
    }

    @Override
    public void onItemClicked(Item item) {
        if (pageView.actionModeIsActive())
            toggleItemSelection(item);
        else
            pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onItemLongClicked(Item item) {
        pageView.setState(iItemsList.ViewState.SELECTION, null, null);
        toggleItemSelection(item);
    }

    @Override
    public void onListFiltered(CharSequence filterText, List<Item> filteredList) {
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
        pageView.setState(iItemsList.ViewState.SELECTION, null, dataAdapter.getSelectedItemCount());
    }

    @Override
    public void onClearSelectionClicked() {
        dataAdapter.clearSelection();
        setInitialViewState();
    }

    @Override
    public void onEditSelectedItemClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onDeleteSelectedItemsClicked() {
        for (Integer index : dataAdapter.getSelectedIndexes())
            dataAdapter.removeItem(dataAdapter.getItem(index));

        setInitialViewState();
    }

    @Override
    public void onActionModeDestroyed() {
        dataAdapter.clearSelection();
        setInitialViewState();
    }


    // Внутренние методы
    private void loadList() {
        pageView.setState(iItemsList.ViewState.PROGRESS, R.string.LIST_TEMPLATE_loading_list, null);

        setRandomList(new iLoadListCallbacks() {
            @Override
            public void onListLoaded() {
                setInitialViewState();
            }
        });
    }

    private interface iLoadListCallbacks {
        void onListLoaded();
    }

    private void setRandomList(iLoadListCallbacks callbacks) {
        List<Item> list = createRandomList();

        if (hasFilterText())
            dataAdapter.setList(list, getFilterText());
        else {
            dataAdapter.setList(list);
        }

        callbacks.onListLoaded();
    }

    private void updatePageTitle() {
        int count = dataAdapter.getListSize();
        pageView.setPageTitle(R.string.LIST_TEMPLATE_title_extended, String.valueOf(count));
    }

    private List<Item> createRandomList() {
        int min = 10;
        int max = 20;
        int randomSize = MyUtils.random(min, max);

        List<Item> list = new ArrayList<>();

        for (int i=1; i<=randomSize; i++) {
            String text = MyUtils.getString(pageView.getAppContext(), R.string.LIST_TEMPLATE_item_name, String.valueOf(i));
            list.add(new Item(text, MyUtils.random(min, max)));
        }

        return list;
    }

    private void toggleItemSelection(Item item) {
        dataAdapter.toggleSelection(dataAdapter.getPositionOf(item));

        int selectedItemsCount = dataAdapter.getSelectedItemCount();

        if (0 == selectedItemsCount) {
            setInitialViewState();
        } else {
            pageView.setState(iItemsList.ViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void setInitialViewState() {
        pageView.setState(iItemsList.ViewState.INITIAL, null, null);
    }
}
