package ru.aakumykov.me.sociocat.tags_list2;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList2 {

    enum SortOrder {
        NAMES_DIRECT, NAMES_REVERSE,
        COUNT_DIRECT, COUNT_REVERSE
    }

    interface TagItemClickListener {
        void onTagClicked(Tag tag);
    }

    interface iPageView extends iBaseView {
        void goShowTag(Tag tag);

        void refreshMenu();
    }

    interface iTagsView {
        void displayList(List<Tag> tagsList);

        List<Tag> getTagsList();
    }

    interface iPresenter {
        void bindViews(iPageView pageView, iTagsView tagsView);
        void unbindViews();

        void startWork();

        void onTagClicked(Tag tag);

        void onSortClicked(SortOrder sortOrder);

        SortOrder getSortOrder();
    }
}
