package ru.aakumykov.me.sociocat.tags_list2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

                sortTagsByCardsCount(tagsList, false);

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


    // Внутренние методы
    private void sortTagsByCardsCount(List<Tag> inputList, boolean reverseOrder) {
        Collections.sort(inputList, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag1, Tag tag2) {
                int cardsCount1 = tag1.getCards().keySet().size();
                int cardsCount2 = tag2.getCards().keySet().size();
                if (cardsCount1 == cardsCount2) return 0;
                return (reverseOrder) ? cardsCount2 - cardsCount1 : cardsCount1 - cardsCount2;
            }
        });
    }

    private void sortTagsByName(List<Tag> inputList, boolean reverseOrder) {
        Collections.sort(inputList, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag1, Tag tag2) {
                String tagName1 = tag1.getName();
                String tagName2 = tag2.getName();
                int res = tagName1.compareToIgnoreCase(tagName2);
                if (0 == res) return 0;
                else {
                    return (reverseOrder) ? -1*res : res;
                }

            }
        });
    }

}
