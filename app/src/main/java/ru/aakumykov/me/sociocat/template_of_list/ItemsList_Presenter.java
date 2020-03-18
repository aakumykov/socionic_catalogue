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


    // iItemsList.iPresenter
    @Override
    public void linkViewAndAdapter(iItemsList.iPageView pageView, iItemsList.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unlinkView() {
        this.pageView = new ItemsList_ViewStub();
        this.dataAdapter = new ItemsList_DataAdapter_Stub();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (null == intent) {
            pageView.showErrorMsg(R.string.data_error, "Intent is null");
            return;
        }

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        updatePageTitle();
    }

    @Override
    public void onRefreshRequested() {
        pageView.finishActionMode();
        setRandomList();
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
        pageView.startActionMode();
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
    public boolean canEditSelectedItem() {
        Item item = dataAdapter.getSingleSelectedItem();

        if (null == item)
            return false;

        int a = item.getCount() % 2;

        return 0 != a;
    }

    @Override
    public boolean canDeleteSelectedItem() {
        return true;
    }

    @Override
    public void onEditSelectedItemClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onDeleteSelectedItemsClicked() {
        for (Item item : dataAdapter.getSelectedItems())
            dataAdapter.removeItem(item);

        pageView.finishActionMode();
    }


    // Внутренние методы
    private void loadList() {
        setRandomList();
    }

    private void setRandomList() {
        List<Item> list = createRandomList();

        if (hasFilterText())
            dataAdapter.setList(list, getFilterText());
        else {
            dataAdapter.setList(list);
        }

        updatePageTitle();

        pageView.hideRefreshThrobber();
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
        dataAdapter.toggleSelection(item, dataAdapter.getPositionOf(item));

        int selectedItemsCount = dataAdapter.getSelectedItemCount();

        if (0 == selectedItemsCount) {
            pageView.finishActionMode();
        } else {
            pageView.showSelectedItemsCount(selectedItemsCount);
        }
    }
}
