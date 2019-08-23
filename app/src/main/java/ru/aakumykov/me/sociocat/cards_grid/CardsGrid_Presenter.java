package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Intent;
import android.text.TextUtils;
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
import ru.aakumykov.me.sociocat.singletons.CardsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
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
//    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton_CF.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton_CF.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();
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

        if (null != intent) {

            String action = intent.getAction() + "";
            pageView.storeAction(action);

            switch (action) {

                case Constants.ACTION_SHOW_NEW_CARDS:
                    checkForNewCards(new iCardsGrid.CheckNewCardsCallbacks() {
                        @Override
                        public void onNewCardsChecked() {

                        }
                    });
                    return;

                case Constants.ACTION_FILTER_BY_TAG:
                    String filteringTag = intent.getStringExtra(Constants.TAG_NAME);
                    loadCardsWithTag(filteringTag);
                    return;
            }
        }

        loadCards(LoadMode.REPLACE, null, null, 0);
    }

    @Override
    public void onRefreshRequested() {

        pageView.showSwipeThrobber();

        checkForNewCards(new iCardsGrid.CheckNewCardsCallbacks() {
            @Override
            public void onNewCardsChecked() {
                pageView.hideSwipeThrobber();
            }
        });
    }

    @Override
    public void onCheckNewCardsClicked() {

        pageView.showToolbarThrobber();

        checkForNewCards(new iCardsGrid.CheckNewCardsCallbacks() {
            @Override
            public void onNewCardsChecked() {
                pageView.hideToolbarThrobber();
            }
        });
    }

    @Override
    public void onLoadOldClicked(int position) {
        try {
            iGridItem lastGridItem = gridView.getGridItem(position-1);
            Card lastCard = (null != lastGridItem) ? (Card) lastGridItem.getPayload() : null;

            gridView.hideLoadOldItem(position);

            loadCardsAfter(lastCard, position);
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
    private void loadCardsAfter(
            Card previousCard,
            int insertPosition
    ) {
        gridView.showThrobber(insertPosition);

        cardsSingleton.loadCardsAfter(previousCard, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                gridView.hideThrobber(insertPosition);
                gridView.insertList(insertPosition, cardsList2gridItemsList(list));
                gridView.showLoadOldItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showToast(R.string.CARDS_GRID_error_loading_cards);
                Log.e(TAG, errorMessage);
            }
        });
    }

    private void loadCards(
            LoadMode loadMode,
            @Nullable String startKey,
            @Nullable String endKey,
            int insertPosition
    )
    {
        gridView.showThrobber(insertPosition);

        iCardsSingleton.ListCallbacks listCallbacks = new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                List<iGridItem> newItemsList = cardsList2gridItemsList(list);

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
        };

        cardsSingleton.loadList(listCallbacks);
    }

    private void loadCardsWithTag(@Nullable String filterTag) {

        if (null != filterTag)
        {
            pageView.setPageTitle(R.string.CARDS_GRID_page_title_tag, filterTag);

            pageView.showProgressMessage(R.string.CARDS_GRID_loading_cards_with_tag, filterTag);

            cardsSingleton.loadList(filterTag, new iCardsSingleton.ListCallbacks() {
                @Override
                public void onListLoadSuccess(List<Card> list) {
                    pageView.hideProgressMessage();
                    pageView.showTagFilter(filterTag);
                    gridView.setList(cardsList2gridItemsList(list));
                }

                @Override
                public void onListLoadFail(String errorMessage) {
                    pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                }
            });
        }
        else {
            pageView.showErrorMsg(R.string.CARDS_GRID_error_there_is_no_tag, "filterTag == null");
        }
    }

    private void checkForNewCards(iCardsGrid.CheckNewCardsCallbacks callbacks) {

        /*Это проверка именно новых, а я сейчас буду делать
        проверку новых вместе с обновлением старых.*/

        iGridItem lastLoadedGridItem = gridView.getLastCardItem();

        if (null != lastLoadedGridItem) {

            Card oldestCard = (Card) lastLoadedGridItem.getPayload();

            cardsSingleton.loadCardsFromNowTo(oldestCard, new iCardsSingleton.ListCallbacks() {
                @Override
                public void onListLoadSuccess(List<Card> list) {
                    callbacks.onNewCardsChecked();
                    gridView.setList(cardsList2gridItemsList(list));
                    /*if (0 == list.size())
                        pageView.showToast(R.string.CARDS_GRID_no_new_cards);
                    else
                        gridView.insertList(0, cardsList2gridItemsList(list));*/
                }

                @Override
                public void onListLoadFail(String errorMessage) {
                    callbacks.onNewCardsChecked();
                    pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                }
            });
        }
        else {
            callbacks.onNewCardsChecked();
        }
//        else {
//            // TODO: ан, нет. Как сюда это - callbacks.onNewCardsChecked() - прикрутить?
//            loadCards(LoadMode.REPLACE, null, null, 0);
//        }

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
            pageView.showTagFilter(filterTag);

            filterTag = filterTag.toLowerCase();

            for (iGridItem item : inputList) {
                Card card = (Card) item.getPayload();
                List<String> cardTags = card.getTagsList(true);
                if (! cardTags.contains(filterTag))
                    resultsList.remove(item);
            }
        }

        return resultsList;
    }

    private void onDeleteCardConfirmed(Card card, iGridItem gridItem) {

        String imageFileName = card.getFileName();
        if (null == imageFileName) {
            pageView.showErrorMsg(R.string.ERROR_deleting_card, "Image file name is NULL");
            return;
        }

        storageSingleton.deleteImage(imageFileName, new iStorageSingleton.FileDeletionCallbacks() {
            @Override
            public void onDeleteSuccess() {

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

            @Override
            public void onDeleteFail(String errorMSg) {
                pageView.showErrorMsg(R.string.ERROR_deleting_card, errorMSg);
            }
        });
    }

    private List<iGridItem> cardsList2gridItemsList(List<Card> cardsList) {
        List<iGridItem> gridItemsList = new ArrayList<>();
        for (Card card : cardsList) {
            iGridItem gridItem = new GridItem_Card();
            gridItem.setPayload(card);
            gridItemsList.add(gridItem);
        }
        return gridItemsList;
    }
}
