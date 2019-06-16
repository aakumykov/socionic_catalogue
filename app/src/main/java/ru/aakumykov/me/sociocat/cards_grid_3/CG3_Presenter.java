package ru.aakumykov.me.sociocat.cards_grid_3;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;

public class CG3_Presenter implements iCG3.Presenter {

    private iCG3.View view;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();


    @Override
    public void linkView(iCG3.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void onWorkBegins() {

        view.showProgressMessage(R.string.CARDS_GRID_loading_cards);

        cardsSingleton.loadList(5, new iCardsSingleton.ListCallbacks() {

            @Override
            public void onListLoadSuccess(List<Card> list) {
                view.hideProgressMessage();
                view.displayList(list);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                view.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }
}
