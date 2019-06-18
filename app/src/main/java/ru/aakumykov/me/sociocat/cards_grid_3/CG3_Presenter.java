package ru.aakumykov.me.sociocat.cards_grid_3;

import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

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
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

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
                0
        );
    }

    @Override
    public void onLoadMoreClicked(int position, String startKey) {
        gridView.hideLoadMoreItem(position);

        loadCards(
                LoadMode.APPEND,
                startKey,
                position
        );
    }

    @Override
    public void onCardClicked(int position) {
        Card card = (Card) gridView.getItem(position).getPayload();
        pageView.goShowCard(card);
    }

    @Override
    public void onCardLongClicked(View view, int position) {

        if (!AuthSingleton.isLoggedIn()) {
            gridView.showPopupMenu(iCG3.MODE_GUEST, view, position);
            return;
        }

        if (usersSingleton.currentUserIsAdmin()) {
            gridView.showPopupMenu(iCG3.MODE_ADMIN, view, position);
            return;
        }

        Card card = (Card) gridView.getItem(position).getPayload();

//        if (card.isCreatedBy(usersSingleton.getCurrentUser()))
        if (usersSingleton.isCardOwner(card)) {
            gridView.showPopupMenu(iCG3.MODE_OWNER, view, position);
            return;
        }
    }

    @Override
    public void onEditClicked(iGridItem gridItem) {
//        if ()
        pageView.showToast("Правка");
    }

    @Override
    public void onDeleteClicked(iGridItem gridItem) {
        pageView.showToast("Удаление");
    }

    @Override
    public void onShareClicked(iGridItem gridItem) {
        pageView.showToast("Распространение");
    }


    // Внутренние методы
    private void loadCards(
            LoadMode loadMode,
            @Nullable String startKey,
            int insertPosition
    )
    {
        gridView.showThrobber();

        cardsSingleton.loadList(startKey, null, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                List<iGridItem> gridItemsList = new ArrayList<>();

                for (Card card : list) {
                    GridItem_Card cardItem = new GridItem_Card();
                    cardItem.setPayload(card);
                    gridItemsList.add(cardItem);
                }

                gridView.hideThrobber();

                switch (loadMode) {
                    case REPLACE:
                        gridView.setList(gridItemsList);
                        break;
                    case APPEND:
                        gridView.appendList(gridItemsList);
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
