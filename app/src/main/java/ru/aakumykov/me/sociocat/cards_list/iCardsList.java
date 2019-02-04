package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

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
