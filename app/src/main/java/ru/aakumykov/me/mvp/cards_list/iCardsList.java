package ru.aakumykov.me.mvp.cards_list;

import android.content.Intent;
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

        void addListItem(Card card);
        void updateListItem(int index, Card card);
        void removeListItem(Card card);

        void processCardCreationResult(Intent data);
        void processCardEditionResult(Intent data);
    }

    interface Presenter {

        void loadList(@Nullable String tagFilter);
        void deleteCard(final Card card);

        // TODO: вынести в общий интерфейс
        void linkView(iCardsList.View view);
        void unlinkView();
    }
}
