package ru.aakumykov.me.mvp.cards_list;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardsList {

    interface View extends iBaseView {
        void displayList(List<Card> list);
        void deleteCardRequest(iDialogCallbacks.Delete callbacks);
    }

    interface Presenter {

        void loadList();
        void deleteCard(final Card card);

        // TODO: вынести в общий интерфейс
        void linkView(iCardsList.View view);
        void unlinkView();
    }
}
