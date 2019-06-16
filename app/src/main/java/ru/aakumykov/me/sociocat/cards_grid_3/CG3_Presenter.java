package ru.aakumykov.me.sociocat.cards_grid_3;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;

public class CG3_Presenter implements iCG3.iPresenter {

    private iCG3.iPageView pageView;
    private iCG3.iGridView gridView;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();


    @Override
    public void linkViews(iCG3.iPageView pageView, iCG3.iGridView gridView) {
        this.pageView = pageView;
        this.gridView = gridView;
    }

    @Override
    public void unlinkViews() {
        this.pageView = null;
        this.gridView = null;
    }

    @Override
    public void onWorkBegins() {

        pageView.showProgressMessage(R.string.CARDS_GRID_loading_cards);

        cardsSingleton.loadList(5, new iCardsSingleton.ListCallbacks() {

            @Override
            public void onListLoadSuccess(List<Card> list) {
                List<iGridItem> gridItems = new ArrayList<>(list);
                pageView.hideProgressMessage();
                gridView.setList(gridItems);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }

    @Override
    public void onCardClicked(int position) {
        Card card = (Card) gridView.getItem(position); //TODO: переделать в getCard()
        pageView.goShowCard(card);
    }
}
