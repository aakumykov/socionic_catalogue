package ru.aakumykov.me.sociocat.tags_list2;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList2 {

    interface iPageView extends iBaseView {
        void goShowTag(Tag tag);
    }

    interface iTagsView {
        void displayList(List<Tag> tagsList);
    }

    interface iPresenter {
        void bindViews(iPageView pageView, iTagsView tagsView);
        void unbindViews();

        void startWork();

        void onTagClicked(Tag tag);

        void onSortByCardsClicked();
        void onSortByNameClicked();
    }
}
