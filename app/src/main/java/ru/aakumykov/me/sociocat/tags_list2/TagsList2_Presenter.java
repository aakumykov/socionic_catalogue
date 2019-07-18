package ru.aakumykov.me.sociocat.tags_list2;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tags_list2.view_stubs.TagsList2_PageViewStub;
import ru.aakumykov.me.sociocat.tags_list2.view_stubs.TagsList2_TagsViewStub;

public class TagsList2_Presenter implements iTagsList2.iPresenter {

    private iTagsList2.iPageView pageView;
    private iTagsList2.iTagsView tagsView;
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();


    @Override
    public void bindViews(iTagsList2.iPageView pageView, iTagsList2.iTagsView tagsView) {
        this.pageView = pageView;
        this.tagsView = tagsView;
    }

    @Override
    public void unbindViews() {
        this.pageView = new TagsList2_PageViewStub();
        this.tagsView = new TagsList2_TagsViewStub();
    }

    @Override
    public void startWork() {
        pageView.showProgressMessage(R.string.TAGS_LIST_loading_list);

        tagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {
            @Override
            public void onTagsListSuccess(List<Tag> tagsList) {
                pageView.hideProgressMessage();
                tagsView.displayList(tagsList);
            }

            @Override
            public void onTagsListFail(String errorMsg) {
                pageView.showErrorMsg(R.string.TAGS_LIST_error_loading_list, errorMsg);
            }
        });
    }

    @Override
    public void onTagClicked(Tag tag) {
        pageView.goShowTag(tag);
    }

    @Override
    public void onSortByCardsClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onSortByNameClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }
}
