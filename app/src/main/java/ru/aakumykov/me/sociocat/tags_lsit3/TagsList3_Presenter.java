package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.tags_lsit3.stubs.TagsList3_ViewStub;

public class TagsList3_Presenter implements iTagsList3.iPresenter {

    private iTagsList3.iPageView pageView;
    private iTagsList3.iDataAdapter dataAdapter;
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private CharSequence filterText;


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
        if (!TextUtils.isEmpty(filterText))
            pageView.restoreSearchViewState(filterText);
    }

    @Override
    public void onPageRefreshRequested() {
        loadList();
    }

    @Override
    public void onTagClicked(Tag tag) {
        pageView.showCardsWithTag(tag);
    }

    @Override
    public void onSortByNameClicked() {
        dataAdapter.sortByName(new iTagsList3.SortingListener() {
            @Override
            public void onSortingComplete() {
                pageView.refreshMenu();
            }
        });
    }

    @Override
    public void onSortByCountClicked() {
        dataAdapter.sortByCount(new iTagsList3.SortingListener() {
            @Override
            public void onSortingComplete() {
                pageView.refreshMenu();
            }
        });
    }

    @Override
    public void onListFiltered(CharSequence filterText, List<Tag> filteredList) {
        this.filterText = filterText;
        this.dataAdapter.setList(filteredList);
    }

    @Override
    public CharSequence getFilterQueryText() {
        return filterText;
    }


    // Внутренние методы
    private void loadList() {

        pageView.showRefreshThrobber();

        tagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {
            @Override
            public void onTagsListSuccess(List<Tag> tagsList) {
                pageView.hideRefreshThrobber();
                dataAdapter.setList(tagsList);
                dataAdapter.deflorate();
            }

            @Override
            public void onTagsListFail(String errorMsg) {
                pageView.hideRefreshThrobber();
                pageView.showErrorMsg(R.string.TAGS_LIST_error_loading_list, errorMsg);
            }
        });
    }

}
