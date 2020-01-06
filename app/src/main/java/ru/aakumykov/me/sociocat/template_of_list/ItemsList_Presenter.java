package ru.aakumykov.me.sociocat.template_of_list;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void onPageRefreshRequested() {
//        pageView.showToast("Ручное обновление страницы");
        setRandomList();
    }

    @Override
    public void onItemClicked(Item item) {
        dataAdapter.removeItem(item);
    }

    @Override
    public void onListFiltered(CharSequence filterText, List<Item> filteredList) {
        dataAdapter.setList(filteredList);
        this.filterText = filterText;
    }

    @Override
    public CharSequence getFilterText() {
        return filterText;
    }


    // Внутренние методы
    private void loadList() {
        setRandomList();
    }

    private void setRandomList() {
        List<Item> list = createRandomList();

        dataAdapter.setList(list);
        dataAdapter.deflorate();

        updatePageTitle();

        pageView.hideRefreshThrobber();
    }

    private void updatePageTitle() {
        int count = dataAdapter.getListSize();
        pageView.setPageTitle(R.string.LIST_TEMPLATE_title_extended, String.valueOf(count));
    }


    private List<Item> createRandomList() {
        int min = 2;
        int max = 20;
        int randomSize = new Random().nextInt((max - min) + 1) + min;

        List<Item> list = new ArrayList<>();

        for (int i=1; i<=randomSize; i++) {
            String text = MyUtils.getString(pageView.getAppContext(), R.string.LIST_TEMPLATE_item_name, String.valueOf(i));
            list.add(new Item(text));
        }

        return list;
    }
}
