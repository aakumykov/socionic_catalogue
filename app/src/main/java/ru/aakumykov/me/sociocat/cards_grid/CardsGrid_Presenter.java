package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.view_stubs.CardsGrid_AdapterStub;
import ru.aakumykov.me.sociocat.cards_grid.view_stubs.CardsGrid_ViewStub;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyDialogs;

public class CardsGrid_Presenter implements iCardsGrig.iPresenter
{
    enum LoadMode {
        REPLACE,
        APPEND
    }

    private final static String TAG = "CG3_Presenter";
    private iCardsGrig.iPageView pageView;
    private iCardsGrig.iGridView gridView;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private List<iGridItem> mList = new ArrayList<>();
    private Integer openedItemPosition;

    @Override
    public void linkViews(iCardsGrig.iPageView pageView, iCardsGrig.iGridView gridView) {
        this.pageView = pageView;
        this.gridView = gridView;

        gridView.restoreList(mList, openedItemPosition);
    }

    @Override
    public void unlinkViews() {
        this.pageView = new CardsGrid_ViewStub();
        this.gridView = new CardsGrid_AdapterStub();
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
        this.openedItemPosition = position;
        pageView.goShowCard(card);
    }

    @Override
    public void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder) {

        if (!AuthSingleton.isLoggedIn()) {
            gridView.showPopupMenu(iCardsGrig.MODE_GUEST, position, view, gridViewHolder);
            return;
        }

        if (usersSingleton.currentUserIsAdmin()) {
            gridView.showPopupMenu(iCardsGrig.MODE_ADMIN, position, view, gridViewHolder);
            return;
        }

        Card card = (Card) gridView.getItem(position).getPayload();

        if (card.isCreatedBy(usersSingleton.getCurrentUser())) {
            gridView.showPopupMenu(iCardsGrig.MODE_OWNER, position, view, gridViewHolder);
            return;
        }
    }

    @Override
    public void onCreateCardClicked(Constants.CardType cardType) {
        pageView.goCreateCard(cardType);
    }

    @Override
    public void onEditCardClicked(iGridItem gridItem) {
        Card card = (Card) gridItem.getPayload();
        int position = gridView.getItemPosition(gridItem);
        pageView.goEditCard(card, position);
    }

    @Override
    public void onDeleteCardClicked(iGridItem gridItem) {
        Card card = (Card) gridItem.getPayload();

        if (!usersSingleton.currentUserIsAdmin()) {
            pageView.showToast(R.string.action_denied);
            return;
        }

        MyDialogs.cardDeleteDialog(
                pageView.getActivity(),
                card.getTitle(),
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        onDeleteCardConfirmed(card, gridItem);
                    }
                }
        );
    }

    @Override
    public void onShareCardClicked(iGridItem gridItem) {
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

                List<iGridItem> newItemsList = new ArrayList<>();

                for (Card card : list) {
                    GridItem_Card cardItem = new GridItem_Card();
                    cardItem.setPayload(card);
                    newItemsList.add(cardItem);
                }

                gridView.hideThrobber();

                switch (loadMode) {
                    case REPLACE:
                        mList.clear();
                        mList.addAll(newItemsList);
                        gridView.setList(mList);
                        break;
                    case APPEND:
                        mList.addAll(newItemsList);
                        gridView.appendList(newItemsList, false, null);
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

    private void onDeleteCardConfirmed(Card card, iGridItem gridItem) {

        cardsSingleton.deleteCard(card, new iCardsSingleton.DeleteCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                gridView.removeItem(gridItem);
            }

            @Override
            public void onCardDeleteError(String msg) {
                pageView.showErrorMsg(R.string.ERROR_deleting_card, msg);
            }
        });
    }
}
