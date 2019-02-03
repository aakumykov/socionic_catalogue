package ru.aakumykov.me.sociocat.tags;

import android.support.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Tag;


public interface iTags {

    interface View {}


    interface ListView extends iBaseView, View {
        void displayTags(List<Tag> list);
        void goShowPage(String tagId);
        void hideSwipeRefresh();
    }

    interface ShowView extends iBaseView, View {
        void displayTag(Tag tag);
        void goCardsListPage(@Nullable String tagFilter);
    }

    interface EditView extends iBaseView, View {

    }


    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        void onTagClicked(Tag tag);

        void loadList();
        void onShowPageReady(String tagKey);
        void onEditPageReady(String tagKey);
    }
}
