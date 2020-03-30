package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.CardType;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.stubs.CardsList_ViewStub;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.DeleteCard_Helper;
import ru.aakumykov.me.sociocat.utils.IntentUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;

public class CardsList_Presenter implements iCardsList.iPresenter {

    private static final String TAG = CardsList_Presenter.class.getSimpleName();

    private iCardsList.iPageView pageView;
    private iCardsList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iCardsList.PageViewState currentPageViewState;
    private Integer currentViewMessageId;
    private Object currentViewMessageDetails;
    private iCardsList.ViewMode currentViewMode;

    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private DataItem currentlyEditedItem;


    @Override
    public void setDataAdapter(iCardsList.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    // iCardsList.iPresenter
    @Override
    public void linkView(iCardsList.iPageView pageView) {
        this.pageView = pageView;
    }

    @Override
    public void unlinkView() {
        this.pageView = new CardsList_ViewStub();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (null == intent) {
            setErrorViewState(R.string.data_error, "Intent is null");
            return;
        }

//        pageView.changeViewMode(currentViewMode);

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        //pageView.changeViewMode(currentViewMode);
        pageView.setViewState(currentPageViewState, currentViewMessageId, currentViewMessageDetails);
    }

    @Override
    public void storeViewState(iCardsList.PageViewState pageViewState, Integer messageId, Object messageDetails) {
        this.currentPageViewState = pageViewState;
        this.currentViewMessageId = messageId;
        this.currentViewMessageDetails = messageDetails;
    }

    @Override
    public void onRefreshRequested() {

        DataItem lastDataItem = dataAdapter.getLastDataItem();

        if (null == lastDataItem) {
            setSuccessViewState();
            return;
        }

        Card card = (Card) lastDataItem.getPayload();

        pageView.setViewState(iCardsList.PageViewState.REFRESHING, null, null);

        cardsSingleton.loadCardsFromNewestTo(card, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                setSuccessViewState();
                dataAdapter.setList(incapsulateObjects2DataItems(list));
                dataAdapter.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
            }
        });
    }

    @Override
    public void onDataItemClicked(DataItem dataItem) {
        if (pageView.actionModeIsActive())
            toggleItemSelection(dataItem);
        else {
            Card card = (Card) dataItem.getPayload();
            pageView.goShowCard(card);
        }
    }

    @Override
    public void onDataItemLongClicked(DataItem dataItem) {
        if (canStartSelection()) {
            pageView.setViewState(iCardsList.PageViewState.SELECTION, null, null);
            toggleItemSelection(dataItem);
        }
    }

    @Override
    public void onLoadMoreClicked() {
        //int scrollPosition = dataAdapter.getDataItemsCount() + 1;

        DataItem lastDataItem = dataAdapter.getLastDataItem();

        if (null != lastDataItem) {
            Card card = (Card) lastDataItem.getPayload();
            loadMoreCards(card);
        }
        else {
            loadList();
        }
    }

    @Override
    public void onListFiltered(CharSequence filterText, List<DataItem> filteredList) {
        dataAdapter.setList(filteredList);
        this.filterText = filterText;
    }

    @Override
    public boolean hasFilterText() {
        return !TextUtils.isEmpty(filterText);
    }

    @Override
    public CharSequence getFilterText() {
        return filterText;
    }

    @Override
    public boolean canStartSelection() {
        return isAdmin();
    }

    @Override
    public boolean canSelectAll() {
        return isAdmin();
    }

    @Override
    public boolean canEditSelectedItem() {
        return isAdmin() && dataAdapter.isSingleItemSelected();
    }

    @Override
    public boolean canDeleteSelectedItem() {
        return isAdmin();
    }

    @Override
    public void onSelectAllClicked() {
        dataAdapter.selectAll(dataAdapter.getDataItemsCount());
        pageView.setViewState(iCardsList.PageViewState.SELECTION, null, dataAdapter.getSelectedItemsCount());
    }

    @Override
    public void onClearSelectionClicked() {
        dataAdapter.clearSelection();
        setSuccessViewState();
    }

    @Override
    public void onEditSelectedItemClicked() {
        currentlyEditedItem = dataAdapter.getSelectedItems().get(0);
        Card card = (Card) currentlyEditedItem.getPayload();
        pageView.goEditCard(card);
        pageView.finishActionMode();
    }

    @Override
    public void onDeleteSelectedItemsClicked() {

        MyDialogs.deleteSelectedCardsDialog(
                pageView.getActivity(),
                R.plurals.CARDS_GRID_delete_selected_cards_title,
                dataAdapter.getSelectedItemsCount(),
                new iMyDialogs.DeleteCallbacks() {
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
                        onDeleteSelectedCardsConfirmed();
                    }
                }
        );
    }

    @Override
    public void onActionModeDestroyed() {
        dataAdapter.clearSelection();
        setSuccessViewState();
    }

    @Override
    public void onChangeLayoutClicked() {

        iCardsList.ViewMode viewMode = dataAdapter.getLayoutMode();

        switch (viewMode) {
            case LIST:
                dataAdapter.setLayoutMode(iCardsList.ViewMode.GRID);
                break;

            case GRID:
                dataAdapter.setLayoutMode(iCardsList.ViewMode.LIST);
                break;

            default:
                throw new RuntimeException("Unknown layout mode");
        }

        pageView.changeViewMode(viewMode);
        pageView.refreshMenu();
    }

    @Override
    public void onNewCardMenuClicked() {
        pageView.showAddNewCardMenu();
    }

    @Override
    public void onNewCardTypeSelected(CardType cardType) {
        pageView.goCreateCard(cardType);
    }

    @Override
    public void onNewCardCreated(@Nullable Intent intent) {
        if (null == intent) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Intent is null");
            return;
        }

        Card card = IntentUtils.extractCard(intent);

        if (null == card) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Card is null");
            return;
        }

        DataItem dataItem = new DataItem<>();
            dataItem.setName(card.getTitle());
            dataItem.setCount(card.getTitle().length());
            dataItem.setPayload(card);

        dataAdapter.addItem(dataItem);
    }

    @Override
    public void onCardEdited(@Nullable Intent data) {
        if (null == data) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Intent is null");
            return;
        }

        Card card = IntentUtils.extractCard(data);
        if (null == card) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Card is null");
            return;
        }

        currentlyEditedItem.setPayload(card);
        dataAdapter.updateItem(currentlyEditedItem);
    }


    // Внутренние методы
    private void loadList() {
        //pageView.setViewState(iCardsList.PageViewState.PROGRESS, null, null);
        dataAdapter.showThrobberItem();

        cardsSingleton.loadCardsFromBeginning(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                setSuccessViewState();
                dataAdapter.hideThrobberItem();
                dataAdapter.setList(incapsulateObjects2DataItems(list));
                dataAdapter.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                dataAdapter.showLoadmoreItem();
            }
        });
    }

    private void loadMoreCards(Card startingFromCard) {
        dataAdapter.showThrobberItem();

        cardsSingleton.loadCardsAfter(startingFromCard, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                dataAdapter.hideThrobberItem();
                dataAdapter.appendList(incapsulateObjects2DataItems(list));
                dataAdapter.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
                dataAdapter.showLoadmoreItem();
            }
        });
    }

    private <T> List<DataItem> incapsulateObjects2DataItems(List<T> objectList) {
        List<DataItem> outputList = new ArrayList<>();
        for (Object object : objectList) {
            DataItem dataItem = new DataItem<Card>();
            dataItem.setPayload(object);
            outputList.add(dataItem);
        }
        return outputList;
    }

    private void getRandomList(iLoadListCallbacks callbacks) {
        List<DataItem> list = createRandomList();

        /*if (hasFilterText())
            dataAdapter.setList(list, getFilterText());
        else {
            dataAdapter.setList(list);
        }*/

        callbacks.onListLoaded(list);
    }

    private interface iLoadListCallbacks {
        void onListLoaded(List<DataItem> list);
    }

    private List<DataItem> createRandomList() {
        int min = 1;
        int max = 100;
        int randomSize = MyUtils.random(1, 10);

        List<DataItem> list = new ArrayList<>();

        for (int i=1; i<=randomSize; i++) {
            String text = MyUtils.getStringWithString(
                    pageView.getAppContext(),
                    R.string.LIST_TEMPLATE_item_name,
                    String.valueOf(MyUtils.random(min, max))
            );
            list.add(new DataItem(text, MyUtils.random(min, max)));
        }

        return list;
    }

    private void toggleItemSelection(DataItem dataItem) {
        dataAdapter.toggleSelection(dataAdapter.getPositionOf(dataItem));

        int selectedItemsCount = dataAdapter.getSelectedItemsCount();

        if (0 == selectedItemsCount) {
            setSuccessViewState();
        } else {
            pageView.setViewState(iCardsList.PageViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void setSuccessViewState() {
        pageView.setViewState(iCardsList.PageViewState.SUCCESS, null, null);
    }

    private void setErrorViewState(int messageId, String errorMessage) {
        pageView.setViewState(iCardsList.PageViewState.ERROR, messageId, errorMessage);
    }

    private boolean isAdmin() {
        return usersSingleton.currentUserIsAdmin();
    }

    private void onDeleteSelectedCardsConfirmed() {
        List<DataItem> selectedItems = dataAdapter.getSelectedItems();

        for (DataItem dataItem : selectedItems)
            deleteCard(dataItem);

        pageView.finishActionMode();
    }

    private void deleteCard(@NonNull DataItem dataItem) {

        Card card = (Card) dataItem.getPayload();

        dataAdapter.setItemIsNowDeleting(dataItem, true);

        DeleteCard_Helper.deleteCard(card.getKey(), new DeleteCard_Helper.iDeletionCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                dataAdapter.removeItem(dataItem);
                pageView.showToast(pageView.getString(R.string.card_deleted_long, card.getTitle()));
            }

            @Override
            public void onCardDeleteError(String errorMsg) {
                dataAdapter.setItemIsNowDeleting(dataItem, false);
                pageView.showToast(pageView.getString(R.string.error_deleting_card_long, card.getTitle()));
                Log.e(TAG, errorMsg);
            }
        });
    }
}
