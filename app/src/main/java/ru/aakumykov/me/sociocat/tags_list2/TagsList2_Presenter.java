package ru.aakumykov.me.sociocat.tags_list2;

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
//    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iTagsList2.SortOrder sortOrder = iTagsList2.SortOrder.NAMES_DIRECT;

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

                sortTagsByCardsCount(tagsList, true);

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
    public void onSortClicked(iTagsList2.SortOrder sortOrder) {

        this.sortOrder = sortOrder;
        List<Tag> list = tagsView.getTagsList();

        switch (sortOrder) {
            case NAMES_DIRECT:
                sortTagsByName(list, true);
                break;

            case NAMES_REVERSE:
                sortTagsByName(list, false);
                break;

            case COUNT_DIRECT:
                sortTagsByCardsCount(list, true);
                break;

            case COUNT_REVERSE:
                sortTagsByCardsCount(list, false);
                break;

            default:
                throw new IllegalArgumentException("Unknown sort order: "+sortOrder);
        }

        pageView.refreshMenu();

        tagsView.displayList(list);
    }

    @Override
    public iTagsList2.SortOrder getSortOrder() {
        return this.sortOrder;
    }


    // Внутренние методы
    private void sortTagsByName(List<Tag> inputList, boolean directOrder) {
        Collections.sort(inputList, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag1, Tag tag2) {
                String tagName1 = tag1.getName();
                String tagName2 = tag2.getName();
                int res = tagName1.compareToIgnoreCase(tagName2);
                if (0 == res) return 0;
                else {
                    return (directOrder) ? res : -1*res;
                }

            }
        });
    }

    private void sortTagsByCardsCount(List<Tag> inputList, boolean directOrder) {
        Collections.sort(inputList, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag1, Tag tag2) {
                int cardsCount1 = tag1.getCardsCount();
                int cardsCount2 = tag2.getCardsCount();
                return (directOrder) ? cardsCount1 - cardsCount2 : cardsCount2 - cardsCount1;
            }
        });
    }

}
