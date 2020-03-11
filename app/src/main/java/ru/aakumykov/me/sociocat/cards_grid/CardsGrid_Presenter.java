package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.stubs.CardsGrid_DataAdapterStub;
import ru.aakumykov.me.sociocat.cards_grid.stubs.CardsGrid_ViewStub;
import ru.aakumykov.me.sociocat.services.NewCardsService;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.DeleteCard_Helper;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_Presenter implements iCardsGrid.iPresenter
{
    enum LoadMode {
        REPLACE,
        APPEND
    }

    private final static String TAG = "CG3_Presenter";
    private iCardsGrid.iPageView pageView;
    private iCardsGrid.iDataAdapter dataAdapter;
    private NewCardsService newCardsService;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private String tagFilter;
    private Card newCardsBoundaryCard;
    private boolean newCardsAreLoadedNow = false;

    @Override
    public void bindComponents(iCardsGrid.iPageView pageView, iCardsGrid.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;

    }

    @Override
    public void unbindComponents() {
        this.pageView = new CardsGrid_ViewStub();
        this.dataAdapter = new CardsGrid_DataAdapterStub();

    }

    @Override
    public void bindNewCardsService(NewCardsService newCardsService) {
        this.newCardsService = newCardsService;
    }

    @Override
    public void unbindNewCardsService() {
        this.newCardsService = null;
    }


    @Override
    public void processInputIntent(@Nullable Intent intent) {
        if (null == intent) {
            pageView.showErrorMsg(R.string.data_error, "Intent is NULL");
            return;
        }

        boolean backButtonEnabled = intent.getBooleanExtra(Constants.BACK_BUTTON_ENABLED, false);
        if (backButtonEnabled)
            pageView.activateUpButton();

        this.tagFilter = intent.getStringExtra(Constants.TAG_NAME);

        if (null != this.tagFilter)
        {
            pageView.activateUpButton();

            if (null != tagFilter)
                loadCardsWithTag(this.tagFilter);
            else
                pageView.showErrorMsg(R.string.CARDS_GRID_error_no_tag_filter, "tagFilter == null");
        }
        else {
            loadCards();
        }
    }

    @Override
    public void onRefreshRequested() {

        pageView.hideMessage();
        pageView.showSwipeThrobber();

        iCardsSingleton.ListCallbacks listCallbacks = new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                pageView.hideSwipeThrobber();
                pageView.onPageRefreshed();
                dataAdapter.setList(cardsList2gridItemsList(list));
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        };

        iGridItem lastCardItem = dataAdapter.getLastCardItem();
        if (null != lastCardItem) {

            Card lastCard = (Card) lastCardItem.getPayload();
            if (null != lastCard) {

                if (null == tagFilter)
                    cardsSingleton.loadCardsFromNewestTo(lastCard, listCallbacks);
                else
                    cardsSingleton.loadCardsWithTagFromNewestTo(this.tagFilter, lastCard, listCallbacks);
            }
        }
    }

    @Override
    public void onLoadMoreClicked(int position) {
        iGridItem lastGridItem = dataAdapter.getGridItem(position - 1);
        Card lastCard = (null != lastGridItem) ? (Card) lastGridItem.getPayload() : null;

        dataAdapter.hideLoadMoreItem(position);
        dataAdapter.showThrobber(position);

        iCardsSingleton.ListCallbacks listCallbacks = new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                dataAdapter.hideThrobber(position);
                dataAdapter.addList(cardsList2gridItemsList(list), position, false, null);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        };

        if (null != this.tagFilter) {
            cardsSingleton.loadCardsWithTagAfter(this.tagFilter, lastCard, listCallbacks);
        }
        else {
            cardsSingleton.loadCardsAfter(lastCard, listCallbacks);
        }
    }

    @Override
    public void onNewCardsAvailable(int count) {
        if (newCardsAreLoadedNow)
            return;

        pageView.showNewCardsNotification(count);

        if (null == newCardsBoundaryCard)
            newCardsBoundaryCard = (Card) dataAdapter.getFirstCardItem().getPayload();
    }

    @Override
    public void onNewCardsAvailableClicked() {
        GridItem_Card cardGridItem = dataAdapter.getFirstCardItem();
        Card latestCard = (Card) cardGridItem.getPayload();

        newCardsAreLoadedNow = true;
        pageView.hideNewCardsNotification();

        pageView.showLoadingNewCardsThrobber();

        cardsSingleton.loadCardsFromNewestTo(latestCard, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                pageView.hideLoadingNewCardsThrobber();
                dataAdapter.addNewCards(cardsList2gridItemsList(list), newCardsBoundaryCard);
                newCardsAreLoadedNow = false;
                pageView.resetNewCardsCounter();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.hideLoadingNewCardsThrobber();
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                newCardsAreLoadedNow = false;
            }
        });
    }

    @Override
    public void onCardClicked(int position) {
        iGridItem gridItem = dataAdapter.getGridItem(position);
        Card card = (Card) gridItem.getPayload();
        pageView.goShowCard(card, position);
    }

    @Override
    public void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder) {

        if (!AuthSingleton.isLoggedIn()) {
            dataAdapter.showPopupMenu(iCardsGrid.MODE_GUEST, position, view, gridViewHolder);
            return;
        }

        if (usersSingleton.currentUserIsAdmin()) {
            dataAdapter.showPopupMenu(iCardsGrid.MODE_ADMIN, position, view, gridViewHolder);
            return;
        }

        Card card = (Card) dataAdapter.getGridItem(position).getPayload();

        if (card.isCreatedBy(usersSingleton.getCurrentUser())) {
            dataAdapter.showPopupMenu(iCardsGrid.MODE_OWNER, position, view, gridViewHolder);
        }
    }

    @Override
    public void onCreateCardClicked(Constants.CardType cardType) {
        pageView.goCreateCard(cardType);
    }

    @Override
    public void onEditCardClicked(iGridItem gridItem) {
        Card card = (Card) gridItem.getPayload();
        int position = dataAdapter.getItemPosition(gridItem);
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

    private List<iGridItem> filterList(List<iGridItem> inputList) {
        String filterTag = this.tagFilter;
        String filterWord = pageView.getCurrentFilterWord();

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (!TextUtils.isEmpty(filterWord))
            resultsList = filterCardsByTitle(filterWord, inputList);

        if (!TextUtils.isEmpty(filterTag))
            resultsList = filterCardsByTag(filterTag, resultsList);

        return resultsList;
    }

    @Override public void onFilteringTagDiscardClicked() {
        pageView.goCardsGrid();
    }

    @Override
    public void processCardCreationResult(@Nullable Intent data) {
        try {
            Card card = data.getParcelableExtra(Constants.CARD);

            // Игнорирую карточку, не подходящую под активную фильтрующую метку
            if (null != tagFilter) {
                if (!card.hasTag(tagFilter)) {
                    pageView.showInfoMsg(
                            R.string.CARDS_GRID_new_card_is_filtered,
                            card.getTitle(),
                            tagFilter
                    );
                    return;
                }
            }

            newCardsService.addLocallyCreatedCard(card.getKey());

            dataAdapter.insertItem(0, new GridItem_Card(card));
            pageView.scroll2position(0);
        }
        catch (Exception e) {
            pageView.showErrorMsg(R.string.CARDS_GRID_error_processing_card, e.getMessage());
            e.printStackTrace();
        }
    }


    // Внутренние методы
    private void loadCards() {
        int insertPosition = 0;

        dataAdapter.showThrobber(0);

        cardsSingleton.loadCards(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                dataAdapter.hideThrobber(insertPosition);
                dataAdapter.setList(cardsList2gridItemsList(list));
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }

    private void loadCardsWithTag(String filterTag) {

        pageView.setPageTitle(R.string.CARDS_GRID_page_title_tag, filterTag);

        pageView.showProgressMessage(R.string.CARDS_GRID_loading_cards_with_tag, filterTag);

        try {
            cardsSingleton.loadCardsWithTag(filterTag, new iCardsSingleton.ListCallbacks() {
                @Override
                public void onListLoadSuccess(List<Card> list) {
                    pageView.hideProgressMessage();
                    pageView.showTagFilter(filterTag);
                    dataAdapter.setList(cardsList2gridItemsList(list));
                }

                @Override
                public void onListLoadFail(String errorMessage) {
                    pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                }
            });
        }
        catch (Exception e) {
            pageView.showErrorMsg(R.string.CARDS_GRID_error_loading_cards, e.getMessage());
            MyUtils.printError(TAG, e);
        }
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

        DeleteCard_Helper.deleteCard(card.getKey(), new DeleteCard_Helper.iDeletionCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                dataAdapter.removeItem(gridItem);
            }

            @Override
            public void onCardDeleteError(String errorMsg) {
                pageView.showErrorMsg(R.string.ERROR_deleting_card, errorMsg);
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
