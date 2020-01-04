package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.tags_lsit3.model.Item;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_Page_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class TagsList3_Presenter implements iTagsList3.iPresenter {

    private iTagsList3.iPageView pageView;
    private iTagsList3.iDataAdapter dataAdapter;

    // iTagsList3.iPresenter
    @Override
    public void linkViewAndAdapter(iTagsList3.iPageView pageView, iTagsList3.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unlinkView() {
        this.pageView = new TagsList3_Page_ViewStub();
        this.dataAdapter = new TagsList3_DataAdapter_Stub();
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
