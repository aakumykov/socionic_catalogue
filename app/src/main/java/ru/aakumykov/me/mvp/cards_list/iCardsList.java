package ru.aakumykov.me.mvp.cards_list;

import android.support.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardsList {

    interface View extends iBaseView {
        void displayList(List<Card> list);
        void displayTagFilter(String text);
        void notifyListChanged();
    }

    interface Presenter {

        void loadList(List<Card> cardsList, @Nullable String tagFilter);
        void deleteCardConfigmed(final Card card);
        void detachDBListener();

        // TODO: вынести в общий интерфейс
        void linkView(iCardsList.View view);
        void unlinkView();
    }
}
