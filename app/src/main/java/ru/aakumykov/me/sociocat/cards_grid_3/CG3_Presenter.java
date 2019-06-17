package ru.aakumykov.me.sociocat.cards_grid_3;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CG3_Presenter implements iCG3.iPresenter
{
    enum LoadMode {
        REPLACE,
        APPEND
    }

    private final static String TAG = "CG3_Presenter";
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
        loadCards(
                LoadMode.REPLACE,
                null,
                null,
                0
        );
    }

    @Override
    public void onCardClicked(int position) {
        Card card = (Card) gridView.getItem(position); //TODO: переделать в getCard()
        pageView.goShowCard(card);
    }

    @Override
    public void onLoadMoreClicked(int position, String startKey) {
            loadCards(
                    LoadMode.APPEND,
                    startKey,
                    null,
                    position
            );
    }


    // Внутренние методы
    private void loadCards(
            LoadMode loadMode,
            @Nullable String startKey,
            @Nullable String endKey,
            int insertPosition
    )
    {
        gridView.showLoadThrobber();

        cardsSingleton.loadList(15, new iCardsSingleton.ListCallbacks() {

            @Override
            public void onListLoadSuccess(List<Card> list) {
                List<iGridItem> gridItems = new ArrayList<>(list);

                gridView.hideLoadThrobber(); // TODO: перенести в методы set*/append* ...

                switch (loadMode) {
                    case REPLACE:
                        gridView.setList(gridItems);
                        break;
                    case APPEND:
                        gridView.appendList(gridItems);
                        break;
                    default:
                        // TODO: показывать ошибку? кидать исключение?
                        Log.e(TAG, "Wrong LoadMode: "+loadMode);
                        break;
                }
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }
}
