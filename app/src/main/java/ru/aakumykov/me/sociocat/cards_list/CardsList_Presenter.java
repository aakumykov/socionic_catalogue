package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.CardType;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.stubs.CardsList_ViewStub;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.DeleteCard_Helper;
import ru.aakumykov.me.sociocat.utils.IntentUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;

import static android.app.Activity.RESULT_OK;

public class CardsList_Presenter implements iCardsList.iPresenter {

    private static final String TAG = CardsList_Presenter.class.getSimpleName();

    private iCardsList.iPageView pageView;
    private iCardsList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iCardsList.ViewState currentViewState;
    private Integer currentViewMessageId;
    private Object currentViewMessageDetails;

    private iCardsList.ViewMode currentViewMode;
    private iCardsList.ToolbarState currentToolbarState;

    private final iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private final iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private DataItem<Card> currentlyEditedItem;

    private int activityRequestCode;
    private int activityResultCode;
    private Intent activityResultData;


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

        String tagName = intent.getStringExtra(Constants.TAG_NAME);

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        //pageView.changeViewMode(currentViewMode);
        pageView.setViewState(currentViewState, currentViewMessageId, currentViewMessageDetails);
        //pageView.setToolbarState(currentToolbarState);
    }

    @Override
    public void storeViewState(iCardsList.ViewState viewState, Integer messageId, Object messageDetails) {
        this.currentViewState = viewState;
        this.currentViewMessageId = messageId;
        this.currentViewMessageDetails = messageDetails;
    }

    @Override
    public void storeViewMode(iCardsList.ViewMode initialViewMode) {
        currentViewMode = initialViewMode;
    }

    @Override
    public iCardsList.ViewMode getViewMode() {
        return currentViewMode;
    }

    @Override
    public void storeToolbarState(iCardsList.ToolbarState toolbarState) {
        this.currentToolbarState = toolbarState;
    }

    @Override
    public iCardsList.ToolbarState getToolbarState() {
        return currentToolbarState;
    }

    @Override
    public void onRefreshRequested() {
        loadListFromBeginning();
    }

    @Override
    public void onDataItemClicked(DataItem<Card> dataItem) {
        if (pageView.actionModeIsActive())
            toggleItemSelection(dataItem);
        else {
            Card card = (Card) dataItem.getPayload();
            pageView.goShowCard(card);
        }
    }

    @Override
    public void onDataItemLongClicked(DataItem<Card> dataItem) {
        if (canSelectItem()) {
            pageView.setViewState(iCardsList.ViewState.SELECTION, null, null);
            toggleItemSelection(dataItem);
        }
    }

    @Override
    public void onCardAuthorClicked(String userId) {
        pageView.goUserProfile(userId);
    }

    @Override
    public void onCardCommentsClicked(Card card) {
        pageView.go2cardComments(card);
    }

    @Override
    public void onRatingWidgetClicked(Card card) {
        pageView.goShowCard(card);
    }

    @Override
    public void onLoadMoreClicked() {
        //int scrollPosition = dataAdapter.getVisibleDataItemsCount() + 1;

        DataItem lastDataItem = dataAdapter.getLastOriginalDataItem();

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
        this.filterText = filterText;
        dataAdapter.setFilteredList(filteredList);
        dataAdapter.showLoadmoreItem();
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
    public boolean canSelectItem() {
        return isAdmin() &&
                (
                        iCardsList.ViewMode.LIST.equals(currentViewMode)
                        ||
                        iCardsList.ViewMode.GRID.equals(currentViewMode)
                );
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
        dataAdapter.selectAll(dataAdapter.getVisibleDataItemsCount());
        pageView.setViewState(iCardsList.ViewState.SELECTION, null, dataAdapter.getSelectedItemsCount());
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
    public void onChangeViewModeClicked(iCardsList.ViewMode viewMode) {
        dataAdapter.setLayoutMode(viewMode);
        pageView.changeViewMode(currentViewMode);
        pageView.refreshMenu();
    }

    @Override
    public void onCreateCardClicked() {
        pageView.showAddNewCardMenu();
    }

    @Override
    public void onCardTypeSelected(CardType cardType) {
        pageView.goCreateCard(cardType);
    }

    @Override
    public boolean isLoggedIn() {
        return AuthSingleton.isLoggedIn();
    }

    @Override
    public void storeFilterText(String text) {
        this.filterText = text;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.activityRequestCode = requestCode;
        this.activityResultCode = resultCode;
        this.activityResultData = data;
    }

    @Override
    public void onStart() {
        processActivityResult();
        forgetActivityResult();
    }


    // Внутренние методы
    private void processActivityResult() {
        switch (activityRequestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult(activityResultCode, activityResultData);
                break;

            case Constants.CODE_SHOW_CARD:
                processCardShowResult(activityResultCode, activityResultData);
                break;

            case Constants.CODE_EDIT_CARD:
                processCardEditionResult(activityResultCode, activityResultData);
                break;

            default:
                Log.e(TAG, "Unknown request code: "+activityRequestCode);
        }
    }

    private void forgetActivityResult() {
        this.activityRequestCode = -1;
        this.activityResultCode = -1;
        this.activityResultData = null;
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK != resultCode)
            return;

        onNewCardCreated(data);
    }

    private void onNewCardCreated(@Nullable Intent intent) {
        if (null == intent) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Intent is null");
            return;
        }

        Card card = IntentUtils.extractCard(intent);

        if (null == card) {
            setErrorViewState(R.string.CARDS_GRID_error_displaying_card, "Card is null");
            return;
        }

        DataItem<Card> dataItem = new DataItem<>();
        dataItem.setName(card.getTitle());
        dataItem.setCount(card.getTitle().length());
        dataItem.setPayload(card);

        dataAdapter.addItem(dataItem);

        pageView.setViewState(iCardsList.ViewState.SUCCESS, -1, null);

        pageView.scrollToPosition(0);
    }

    private void processCardShowResult(int activityResultCode, @Nullable Intent activityResultData) {
        if (activityResultCode == RESULT_OK) {
            if (null != activityResultData) {

                Card card = activityResultData.getParcelableExtra(Constants.CARD);

                String action = activityResultData.getAction();

                switch (action) {
                    // TODO: ACTION_EDIT
                    case Intent.ACTION_VIEW:
                        if (null != card)
                            updateCardInList(card);
                        break;

                    case Intent.ACTION_DELETE:
                        if (null != card)
                            deleteCardFromList(card);
                        break;
                }
            }
        }
    }

    private void updateCardInList(@NonNull Card card) {
        dataAdapter.updateItemWithCard(card);
    }

    private void deleteCardFromList(@NonNull Card card) {
        dataAdapter.deleteItemWithCard(card);
        pageView.showToast(R.string.CARDS_LIST_card_has_been_deleted);
    }


    private void processCardEditionResult(int resultCode, @Nullable Intent data) {
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

    private void loadList() {
        dataAdapter.showThrobberItem();
        loadListFromBeginning();
    }

    private void loadListFromBeginning() {

        cardsSingleton.loadFirstPortion(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                dataAdapter.hideThrobberItem();

                List<DataItem> dataItemsList = incapsulateObjects2DataItems(list);

                if (hasFilterText())
                    dataAdapter.setListAndFilter(dataItemsList, getFilterText());
                else
                    dataAdapter.setList(dataItemsList);

                dataAdapter.showLoadmoreItem();

                setSuccessViewState();
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

                List<DataItem> dataItemsList = incapsulateObjects2DataItems(list);

                if (hasFilterText())
                    dataAdapter.appendListAndFilter(dataItemsList, getFilterText());
                else
                    dataAdapter.appendList(dataItemsList);

                dataAdapter.showLoadmoreItem();

                setSuccessViewState();
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
            pageView.setViewState(iCardsList.ViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void setSuccessViewState() {
        pageView.setViewState(iCardsList.ViewState.SUCCESS, null, null);
    }

    private void setErrorViewState(int messageId, String errorMessage) {
        pageView.setViewState(iCardsList.ViewState.ERROR, messageId, errorMessage);
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
//                pageView.showToast(pageView.getString(R.string.card_deleted_long, card.getTitle()));
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
