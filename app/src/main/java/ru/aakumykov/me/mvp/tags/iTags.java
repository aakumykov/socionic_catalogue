package ru.aakumykov.me.mvp.tags;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Tag;


public interface iTags {

    interface View {}


    interface ListView extends iBaseView, View {
        void displayTags(List<Tag> list);
        void goShowPage(String tagId);
    }

    interface ShowView extends iBaseView, View {
        void displayTag(Tag tag);
        void displayCards(List<Card> cardsList);
    }

    interface EditView extends iBaseView, View {

    }


    interface Presenter {
        void linkView(View view) throws IllegalArgumentException;
        void unlinkView();

        void onTagClicked(Tag tag);

        void onListPageReady();
        void onShowPageReady(String tagKey);
        void onEditPageReady(String tagKey);
    }
}
