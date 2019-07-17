package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
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

public class CardsGrid_Presenter implements iCardsGrid.iPresenter
{
    enum LoadMode {
        REPLACE,
        APPEND
    }

    private final static String TAG = "CG3_Presenter";
    private iCardsGrid.iPageView pageView;
    private iCardsGrid.iGridView gridView;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private String filterTag;
    private String filterWord;

    @Override
    public void linkViews(iCardsGrid.iPageView pageView, iCardsGrid.iGridView gridView) {
        this.pageView = pageView;
        this.gridView = gridView;

        //gridView.restoreList(mList, openedItemPosition);
    }

    @Override
    public void unlinkViews() {
        this.pageView = new CardsGrid_ViewStub();
        this.gridView = new CardsGrid_AdapterStub();
    }


    @Override
    public void processInputIntent(@Nullable Intent intent) {

        this.filterTag = (null == intent) ? null : intent.getStringExtra(Constants.TAG_NAME);

        if (null != filterTag)
            pageView.setPageTitle(R.string.CARDS_GRID_page_title_tag, filterTag);

        loadCards(
                LoadMode.REPLACE,
                null,
                null,
                0
        );
    }

    @Override
    public void onLoadMoreClicked(int position) {
        try {
            iGridItem itemBefore = gridView.getItemBeforeLoadmore(position);
            Card lastCardBefore = (null != itemBefore) ? (Card) itemBefore.getPayload() : null;
            String startKey = (null != lastCardBefore) ? lastCardBefore.getKey() : null;

            iGridItem itemAfter = gridView.getItemAfterLoadmore(position);
            Card cardAfter = (null != itemAfter) ? (Card) itemAfter.getPayload() : null;
            String endKey = (null != cardAfter) ? cardAfter.getKey() : null;

            gridView.hideLoadMoreItem(position);

            loadCards(
                    LoadMode.APPEND,
                    startKey,
                    endKey,
                    position
            );
        }
        catch (Exception e) {
            pageView.showErrorMsg(R.string.CARDS_GRID_loadmore_error, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCardClicked(int position) {
        iGridItem gridItem = gridView.getGridItem(position);
        Card card = (Card) gridItem.getPayload();
//        this.openedItemPosition = position;
        pageView.goShowCard(card);
    }

    @Override
    public void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder) {

        if (!AuthSingleton.isLoggedIn()) {
            gridView.showPopupMenu(iCardsGrid.MODE_GUEST, position, view, gridViewHolder);
            return;
        }

        if (usersSingleton.currentUserIsAdmin()) {
            gridView.showPopupMenu(iCardsGrid.MODE_ADMIN, position, view, gridViewHolder);
            return;
        }

        Card card = (Card) gridView.getGridItem(position).getPayload();

        if (card.isCreatedBy(usersSingleton.getCurrentUser())) {
            gridView.showPopupMenu(iCardsGrid.MODE_OWNER, position, view, gridViewHolder);
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

    @Override
    public List<iGridItem> filterList(List<iGridItem> inputList) {
        String filterTag = this.filterTag;
        String filterWord = pageView.getCurrentFilterWord();

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (!TextUtils.isEmpty(filterWord))
            resultsList = filterCardsByTitle(filterWord, inputList);

        if (!TextUtils.isEmpty(filterTag))
            resultsList = filterCardsByTag(filterTag, resultsList);

        return resultsList;
    }

    @Override
    public void onFilteringTagDiscardClicked() {
        pageView.goCardsGrid();
    }


    // Внутренние методы
    private void loadCards(
            LoadMode loadMode,
            @Nullable String startKey,
            @Nullable String endKey,
            int insertPosition
    )
    {
        gridView.showThrobber(insertPosition);

        cardsSingleton.loadList(startKey, endKey, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                List<iGridItem> newItemsList = new ArrayList<>();

                for (Card card : list) {
                    GridItem_Card cardItem = new GridItem_Card();
                    cardItem.setPayload(card);
                    newItemsList.add(cardItem);
                }

                newItemsList = filterList(newItemsList);

                gridView.hideThrobber(insertPosition);

                switch (loadMode) {
                    case REPLACE:
                        gridView.setList(newItemsList);
                        break;

                    case APPEND:
                        gridView.addList(newItemsList, insertPosition, false, null);
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown loadMode: "+loadMode);
                }
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }

    private List<iGridItem> filterCardsByTitle(@Nullable String filterWord, final List<iGridItem> inputList) {

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (null != filterWord) {
            filterWord = filterWord.toLowerCase();

            for (iGridItem item : inputList) {
                Card card = (Card) item.getPayload();
                String cardTitle = card.getTitle().toLowerCase();
                if (!cardTitle.contains(filterWord))
                    resultsList.remove(item);
            }
        }

        return resultsList;
    }

    private List<iGridItem> filterCardsByTag(@Nullable String filterTag, final List<iGridItem> inputList) {

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (null != filterTag) {
            pageView.showFilterTag(filterTag);

            filterTag = filterTag.toLowerCase();

            for (iGridItem item : inputList) {
                Card card = (Card) item.getPayload();
                HashMap<String, Boolean> cardTags = card.getTags();

                if (! cardTags.containsKey(filterTag)) {
                    resultsList.remove(item);
                }
                else {
                    Boolean tag = cardTags.get(filterTag);
                    if (null == tag || !tag) {
                        resultsList.remove(item);
                    }
                }
            }
        }

        return resultsList;
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
