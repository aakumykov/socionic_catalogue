package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_ViewStub;

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
        this.pageView = new TagsList3_ViewStub();
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

    }

    @Override
    public void onTagClicked(Tag tag) {
        dataAdapter.removeTag(tag);
    }


    // Внутренние методы
    private void loadList() {

    }

    private void updatePageTitle() {
        int count = dataAdapter.getListSize();
        pageView.setPageTitle(R.string.LIST_TEMPLATE_title_extended, String.valueOf(count));
    }


}
