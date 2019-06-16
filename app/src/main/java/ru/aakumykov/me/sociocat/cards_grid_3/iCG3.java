package ru.aakumykov.me.sociocat.cards_grid_3;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCG3 {

    interface View extends iBaseView {
        void displayList(List<Card> list);
    }

    interface Presenter {
        void linkView(iCG3.View view);
        void unlinkView();

        void onWorkBegins();
    }
}
