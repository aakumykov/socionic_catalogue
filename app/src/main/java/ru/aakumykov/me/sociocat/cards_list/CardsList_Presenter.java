package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.stubs.CardsList_DataAdapter_Stub;
import ru.aakumykov.me.sociocat.cards_list.stubs.CardsList_ViewStub;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.utils.DeleteCard_Helper;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsList_Presenter implements iCardsList.iPresenter {

    private static final String TAG = CardsList_Presenter.class.getSimpleName();

    private iCardsList.iPageView pageView;
    private iCardsList.iDataAdapter dataAdapter;
    private CharSequence filterText;

    private iCardsList.ViewState currentViewState;
    private Integer currentViewMessageId;
    private Object currentViewMessageDetails;
    private iCardsList.LayoutMode currentLayoutMode;

    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();


    // iCardsList.iPresenter
    @Override
    public void linkViewAndAdapter(iCardsList.iPageView pageView, iCardsList.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unlinkViewAndAdapter() {
        this.pageView = new CardsList_ViewStub();
        this.dataAdapter = new CardsList_DataAdapter_Stub();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (null == intent) {
            showErrorViewState(R.string.data_error, "Intent is null");
            return;
        }

        pageView.changeLayout(currentLayoutMode);

        loadList();
    }

    @Override
    public void onConfigurationChanged() {
        pageView.changeLayout(currentLayoutMode);
        pageView.setViewState(currentViewState, currentViewMessageId, currentViewMessageDetails);
    }

    @Override
    public void storeViewState(iCardsList.ViewState viewState, Integer messageId, Object messageDetails) {
        this.currentViewState = viewState;
        this.currentViewMessageId = messageId;
        this.currentViewMessageDetails = messageDetails;
    }

    @Override
    public iCardsList.LayoutMode getCurrentLayoutMode() {
        return currentLayoutMode;
    }

    @Override
    public void onRefreshRequested() {

        DataItem lastDataItem = dataAdapter.getLastDataItem();

        if (null == lastDataItem) {
            showSuccessViewState();
            return;
        }

        Card card = (Card) lastDataItem.getPayload();

        pageView.setViewState(iCardsList.ViewState.REFRESHING, null, null);

        cardsSingleton.loadCardsFromNewestTo(card, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                showSuccessViewState();
                dataAdapter.setList(incapsulateObjects2DataItems(list));
                dataAdapter.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                showErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
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
        pageView.setViewState(iCardsList.ViewState.SELECTION, null, null);
        toggleItemSelection(dataItem);
    }

    @Override
    public void onLoadMoreClicked() {
        //int scrollPosition = dataAdapter.getListSize() + 1;

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
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canEditSelectedItem() {
        Integer index = dataAdapter.getSingleSelectedItemIndex();

        return null != index;
    }

    @Override
    public boolean canDeleteSelectedItem() {
        // TODO: реализация
        return true;
    }

    @Override
    public void onSelectAllClicked() {
        dataAdapter.selectAll(dataAdapter.getListSize());
        pageView.setViewState(iCardsList.ViewState.SELECTION, null, dataAdapter.getSelectedItemCount());
    }

    @Override
    public void onClearSelectionClicked() {
        dataAdapter.clearSelection();
        showSuccessViewState();
    }

    @Override
    public void onEditSelectedItemClicked() {
        pageView.showToast(R.string.not_implemented_yet);
    }

    @Override
    public void onDeleteSelectedItemsClicked() {
        List<DataItem> selectedItems = dataAdapter.getSelectedItems();

        pageView.finishActionMode();

        for (DataItem dataItem : selectedItems)
            deleteCard(dataItem);
    }

    @Override
    public void onActionModeDestroyed() {
        dataAdapter.clearSelection();
        showSuccessViewState();
    }

    @Override
    public void onChangeLayoutClicked() {
        if (null == currentLayoutMode || iCardsList.LayoutMode.GRID.equals(currentLayoutMode))
            currentLayoutMode = iCardsList.LayoutMode.LIST;
        else
            currentLayoutMode = iCardsList.LayoutMode.GRID;

        pageView.changeLayout(currentLayoutMode);
        pageView.refreshMenu();
    }


    // Внутренние методы
    private void loadList() {
        //pageView.setViewState(iCardsList.ViewState.PROGRESS, null, null);
        dataAdapter.showThrobberItem();

        cardsSingleton.loadCardsFromBeginning(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                showSuccessViewState();
                dataAdapter.hideThrobberItem();
                dataAdapter.setList(incapsulateObjects2DataItems(list));
                dataAdapter.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                showErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
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
                showErrorViewState(R.string.CARDS_GRID_error_loading_cards, errorMessage);
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
            String text = MyUtils.getString(
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

        int selectedItemsCount = dataAdapter.getSelectedItemCount();

        if (0 == selectedItemsCount) {
            showSuccessViewState();
        } else {
            pageView.setViewState(iCardsList.ViewState.SELECTION, null, selectedItemsCount);
        }
    }

    private void showSuccessViewState() {
        pageView.setViewState(iCardsList.ViewState.SUCCESS, null, null);
    }

    private void showErrorViewState(int messageId, String errorMessage) {
        pageView.setViewState(iCardsList.ViewState.ERROR, messageId, errorMessage);
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
