package ru.aakumykov.me.sociocat.b_cards_list;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ListUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.CancelableProgressViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.ErrorViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.RefreshingViewState;
import ru.aakumykov.me.sociocat.b_cards_list.enums.eCardsList_SortingMode;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_ItemClickListener;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_View;
import ru.aakumykov.me.sociocat.b_cards_list.list_items.Card_ListItem;
import ru.aakumykov.me.sociocat.b_cards_list.list_utils.CardsList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.b_cards_list.stubs.CardsList_ViewStub;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsOfUser_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCardsOfUser_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCards_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.SimpleCardsList_ViewState;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.ComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.utils.IntentUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.SimpleYesNoDialog;

public class CardsList_Presenter extends BasicMVPList_Presenter implements iCardsList_ItemClickListener {

    private static final String TAG = CardsList_Presenter.class.getSimpleName();
    private final CardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();
    private String mTagFilter;
    private User mUserFilter;
    private boolean mHasParent;


    public CardsList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();

        mPageView.runDelayed(
                () -> {
                    makeStartDecision();
                },
                500L
        );
    }

    @Override
    public void unbindViews() {
        mPageView = new CardsList_ViewStub();
    }

    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {

        if (sortingMode instanceof eCardsList_SortingMode) {
            switch ((eCardsList_SortingMode) sortingMode) {
                case BY_COMMENTS:
                case BY_RATING:
                    return eSortingOrder.REVERSE;
                default:
                    return eSortingOrder.DIRECT;
            }
        }

        if (sortingMode instanceof eBasicSortingMode) {
            switch ((eBasicSortingMode) sortingMode) {
                case BY_DATE:
                    return eSortingOrder.REVERSE;
            }
        }

        return eSortingOrder.DIRECT;
    }

    @Override
    protected void onRefreshRequested() {
        refreshCardsList(mTagFilter);
    }

    @Override
    public void onItemClicked(BasicMVPList_DataViewHolder dataViewHolder) {

        if (mListView.isSelectionMode()) {
            onSelectItemClicked(dataViewHolder);
            return;
        }

        Card card = getCardForViewHolder(dataViewHolder);
        ((iCardsList_View) mPageView).goShowingCard(card);
    }

    @Override
    public void onItemLongClicked(BasicMVPList_DataViewHolder dataViewHolder) {
        onSelectItemClicked(dataViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder) {

        if (null != mTagFilter) {
            loadMoreCardsWithTag();
            return;
        }

        if (null != mUserFilter) {
            loadMoreCardsOfUser();
            return;
        }

        loadMoreCards();
    }

    @Override
    protected BasicMVPList_ItemsTextFilter getItemsTextFilter() {
        return new CardsList_ItemsTextFilter();
    }


    public void onCardEdited(@Nullable Card oldCard, @Nullable Card newCard) {

        if (null != oldCard && null != newCard)
        {
            Card_ListItem newCardListItem = new Card_ListItem(newCard);

            int position = mListView.findAndUpdateItem(newCardListItem, new iBasicList.iFindItemComparisionCallback() {
                @Override
                public boolean onCompareWithListItemPayload(Object itemPayload) {
                    Card cardFromList = (Card) itemPayload;
                    return cardFromList.equals(oldCard);
                }
            });

            mListView.highlightItem(position);
        }
    }

    public void onCardDeleted(@Nullable Card card) {
        if (null == card)
            return;

        mListView.findAndRemoveItem(new iBasicList.iFindItemComparisionCallback() {
            @Override
            public boolean onCompareWithListItemPayload(Object itemPayload) {
                Card cardFromList = (Card) itemPayload;
                return (cardFromList.equals(card));
            }
        });
    }

    public void onFABClicked() {
        ((CardsList_View) mPageView).showAddNewCardMenu();
    }

    public void onAddNewCardClicked(int itemId) {
        eCardType cardType = null;

        if (R.id.actionAddTextCard == itemId)
            cardType = eCardType.TEXT_CARD;
        else if (R.id.actionAddImageCard == itemId)
            cardType = eCardType.IMAGE_CARD;
        else if (R.id.actionAddAudioCard == itemId)
            cardType = eCardType.AUDIO_CARD;
        else if (R.id.actionAddVideoCard == itemId)
            cardType = eCardType.VIDEO_CARD;
        else
            throw new RuntimeException("Unsupported menu itemId: "+itemId);

        ((CardsList_View) mPageView).goCreateCard(cardType);
    }

    public void onEditCardClicked() {

    }

    public void onCardCreated(@Nullable Card newCard) {
        if (null != newCard) {
            int insertPosition = 0;
            mListView.insertItem(insertPosition, new Card_ListItem(newCard));
            mPageView.scroll2position(insertPosition);
            mListView.highlightItem(insertPosition);
        }
    }

    public void onCloseTagFilterClicked() {
        mTagFilter = null;
        ((iCardsList_View) mPageView).goShowAllCards();
    }



    private void makeStartDecision() {

        Intent intent = mPageView.getInputIntent();

        boolean isViewAction = IntentUtils.intentActionIs(Intent.ACTION_VIEW, intent);
        mHasParent = isViewAction;

        if (isViewAction) {
            if (intent.hasExtra(Constants.TAG_NAME)) {
                loadCardsWithTag(intent, false);
                return;
            }

            if (intent.hasExtra(Constants.USER)) {
                loadCardsOfUser(intent, false);
                return;
            }
        }

        loadCards();
    }


    private void loadCards() {

        setViewState(new LoadingCards_ViewState(mHasParent));

        mCardsSingleton.loadFirstPortion(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                setViewState(new SimpleCardsList_ViewState(mHasParent));

                mListView.setList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private void loadMoreCards() {

        showThrobberItem();

        BasicMVPList_DataItem tailDataItem = mListView.getTailDataItem();
        if (null == tailDataItem) {
            showErrorLoadingMore();
            return;
        }

        Card tailCard = (Card) tailDataItem.getPayload();

        mCardsSingleton.loadCardsAfter(tailCard, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                if (0 == list.size()) {
                    showNoMoreCards();
                    return;
                }

                int position2scroll = mListView.getVisibleListSize();

                List<BasicMVPList_ListItem> list2append = ListUtils.incapsulateObjects2basicItemsList(list, object -> new Card_ListItem((Card) object));

                if (mListView.isFiltered())
                    mListView.appendList(list2append, (allItemsCount, addedItemsCount, filteredOutItemsCount) -> notifyAboutFilteredOutItems(allItemsCount, addedItemsCount, filteredOutItemsCount));
                else
                    mListView.appendList(list2append);

                updateSelectionModeMenu();
                mPageView.scroll2position(position2scroll);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                mPageView.showToast(R.string.CARDS_LIST_error_loading_list);
                mListView.hideThrobberItem();
                mListView.showLoadmoreItem();
            }
        });
    }


    private void loadCardsWithTag(@NonNull Intent intent, boolean isOnRefreshing) {

        mTagFilter = intent.getStringExtra(Constants.TAG_NAME);
        if (null == mTagFilter) {
            setErrorViewState(R.string.CARDS_LIST_error_tag_name_missing, "Нет имени метки");
            return;
        }

        if (isOnRefreshing)
            setViewState(new RefreshingViewState());
        else
            setViewState(new LoadingCardsWithTag_ViewState(mTagFilter));

        mCardsSingleton.loadCardsWithTag(mTagFilter, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                setViewState(new CardsWithTag_ViewState(mTagFilter));

                mListView.setList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.TAGS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private void loadMoreCardsWithTag() {

        showThrobberItem();

        BasicMVPList_DataItem tailDataItem = mListView.getTailDataItem();
        if (null == tailDataItem) {
            showErrorLoadingMore();
            return;
        }

        Card card = (Card) tailDataItem.getPayload();

        mCardsSingleton.loadCardsWithTagAfter(mTagFilter, card, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                setViewState(new CardsWithTag_ViewState(mTagFilter));

                if (0 == list.size()) {
                    showNoMoreCards();
                    return;
                }

                int position2scroll = mListView.getVisibleListSize();

                mListView.appendList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));

                updateSelectionModeMenu();

                mPageView.scroll2position(position2scroll);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }


    private void loadCardsOfUser(@NonNull Intent intent, boolean isOnRefreshing) {

        mUserFilter = intent.getParcelableExtra(Constants.USER);
        if (null == mUserFilter) {
            setErrorViewState(R.string.CARDS_LIST_error_user_key_missing, "Отсутствует id или имя пользователя");
            return;
        }

        String userId = mUserFilter.getKey();
        String userName = mUserFilter.getName();

        if (isOnRefreshing)
            setViewState(new RefreshingViewState());
        else
            setViewState(new LoadingCardsOfUser_ViewState(userName));

        mCardsSingleton.loadCardsOfUser(userId, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                setViewState(new CardsOfUser_ViewState(userName));
                mListView.setList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setViewState(new ErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage));
            }
        });
    }

    private void loadMoreCardsOfUser() {

        showThrobberItem();

        BasicMVPList_DataItem tailDataItem = mListView.getTailDataItem();
        if (null == tailDataItem) {
            showErrorLoadingMore();
            return;
        }

        Card card = (Card) tailDataItem.getPayload();

        mCardsSingleton.loadCardsOfUserAfter(card, mUserFilter, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                if (0 == list.size()) {
                    showNoMoreCards();
                    return;
                }

                int position2scroll = mListView.getVisibleListSize();

                mListView.appendList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));

                updateSelectionModeMenu();

                mPageView.scroll2position(position2scroll);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }


    private Card getCardForViewHolder(BasicMVPList_DataViewHolder basicDataViewHolder) {
        int index = basicDataViewHolder.getAdapterPosition();
        BasicMVPList_ListItem listItem = mListView.getItem(index);
        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) listItem;
        return (Card) dataItem.getPayload();
    }

    private void showThrobberItem() {
        mListView.hideLoadmoreItem();
        mListView.showThrobberItem();
    }

    private void showNoMoreCards() {
        mListView.hideThrobberItem();
        mListView.showLoadmoreItem(R.string.CARDS_LIST_no_more_cards_with_tag);
    }


    private void refreshCardsList(@Nullable String tagFilter) {

        BasicMVPList_DataItem tailDataItem = mListView.getTailDataItem();

        if (null == tailDataItem) {
            loadCards();
            return;
        }

        Card tailCard = (Card) tailDataItem.getPayload();

        setViewState(new LoadingCards_ViewState(mHasParent));

        iCardsSingleton.ListCallbacks listCallbacks = new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                if (null == tagFilter)
                    setViewState(new SimpleCardsList_ViewState(mHasParent));
                else
                    setViewState(new CardsWithTag_ViewState(tagFilter));

                List<BasicMVPList_ListItem> list2set = ListUtils.incapsulateObjects2basicItemsList(list, (payload) -> new Card_ListItem((Card) payload));

                if (mListView.isFiltered())
                    mListView.setList(list2set, (allItemsCount, addedItemsCount, filteredOutItemsCount) -> notifyAboutFilteredOutItems(allItemsCount, addedItemsCount, filteredOutItemsCount));
                else
                    mListView.setList(list2set);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        };

        if (null != tagFilter)
            mCardsSingleton.loadCardsWithTagFromNewestTo(tagFilter, tailCard, listCallbacks);
        else {
            if (mListView.isFiltered()) {
                // Если карточки отфильтрованы, то обновляю весь список, чтобы получить тот же диапазон карточек,
                // потому что много раз подгружать неудобно (ведь при подгрузке они фильтруются).
                mCardsSingleton.loadCardsFromNewestTo(tailCard, listCallbacks);
            }
            else {
                // Если не отфильтрованы, гружу как при первом запуске
                mCardsSingleton.loadFirstPortion(listCallbacks);
            }
        }
    }

    private void notifyAboutFilteredOutItems(int allItemsCount, int addedItemsCount, int filteredOutItemsCount) {

        Context context = mPageView.getLocalContext();

        String addedCardsMsg = TextUtils.getPluralString(context, R.plurals.cards_are_added, addedItemsCount);
        String filteredOutCardsMsg = TextUtils.getPluralString(context, R.plurals.cards_are_filtered_out, filteredOutItemsCount);

        String msg = TextUtils.getText(
                context,
                R.string.two_words_with_new_line,
                addedCardsMsg,
                filteredOutCardsMsg
        );

        MyUtils.showCustomToast(context, msg);
    }


    public boolean canEditCard() {
        return isAdmin();
    }

    public boolean canDeleteCard() {
        return isAdmin();
    }

    private boolean isAdmin() {
        return UsersSingleton.getInstance().currentUserIsAdmin();
    }

    public void onDeleteMenuItemClicked() {

        int count = mListView.getSelectedItemsCount();

        if (count > Constants.MAX_CARDS_AT_ONCE_DELETE_COUNT) {
            String msg = TextUtils.getPluralString(
                    mPageView.getLocalContext(),
                    R.plurals.CARDS_LIST_cannot_delete_more_tags_at_once,
                    Constants.MAX_CARDS_AT_ONCE_DELETE_COUNT
            );
            mPageView.showToast(msg);
            return;
        }

        List<BasicMVPList_DataItem> selectedItems = mListView.getSelectedItems();
        StringBuilder messageBuilder = new StringBuilder();
        int counter = 1;
        for (BasicMVPList_DataItem dataItem : selectedItems) {
            Card card = (Card) dataItem.getPayload();

            messageBuilder.append(counter++);
            messageBuilder.append(") ");

            messageBuilder.append(card.getTitle());

            messageBuilder.append("\n");
        }

        String title = TextUtils.getPluralString(
                mPageView.getLocalContext(),
                R.plurals.CARDS_LIST_deleting_dialog_title,
                count
        );

        SimpleYesNoDialog.show(
                mPageView.getLocalContext(),
                title,
                messageBuilder.toString(),
                new SimpleYesNoDialog.AbstractCallbacks() {
                    @Override
                    public void onYes() {
                        super.onYes();
                        onCardsDeletionConfirmed();
                    }
                }
        );
    }

    private void onCardsDeletionConfirmed() {

        if (!isAdmin()) {
            mPageView.showSnackbar(R.string.CARDS_LIST_you_cannot_delete_cards, R.string.SNACKBAR_got_it, 10000);
            mListView.clearSelection();
            setNeutralViewState();
            return;
        }

        deleteCardsFromList(mListView.getSelectedItems());
    }

    private void deleteCardsFromList(List<BasicMVPList_DataItem> deletedItemsList) {

        if (hasInterruptFlag()) {
            mPageView.showToast(R.string.CARDS_LIST_deletion_process_interrupted);
            mListView.clearSelection();
            setNeutralViewState();
            clearInterruptFlag();
            return;
        }

        if (0 == deletedItemsList.size()) {
//            mPageView.showToast(R.string.CARDS_LIST_selected_card_are_processed);
            setNeutralViewState();
            return;
        }

        BasicMVPList_DataItem dataItem = deletedItemsList.get(0);
        deletedItemsList.remove(0);
        Card card = (Card) dataItem.getPayload();

        String msg = TextUtils.getText(mPageView.getLocalContext(), R.string.CARDS_LIST_deleting_card, card.getName());
        setViewState(new CancelableProgressViewState(msg));

        mComplexSingleton.deleteCard(card, new ComplexSingleton.iComplexSingleton_CardDeletionCallbacks() {
            @Override
            public void onCardDeleteSuccess(@NonNull Card card) {
                mPageView.showStyledToast( mPageView.getText(R.string.card_has_been_deleted, card.getTitle()) );
                mListView.removeItem(dataItem);
                deleteCardsFromList(deletedItemsList);
            }

            @Override
            public void onCardDeleteFailed(@NonNull String errorMsg) {
                setViewState(new ErrorViewState(mPageView.getText(R.string.CARDS_LIST_error_deleting_card, card.getTitle()), errorMsg));
                Log.e(TAG, errorMsg);
            }
        });
    }

    private void showErrorLoadingMore() {
        mPageView.runDelayed(() -> {
            mListView.hideThrobberItem();
            mListView.showLoadmoreItem();
        }, 500);
    }
}
