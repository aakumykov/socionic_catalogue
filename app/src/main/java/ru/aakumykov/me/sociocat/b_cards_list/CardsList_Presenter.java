package ru.aakumykov.me.sociocat.b_cards_list;

import android.content.Intent;

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
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.ListFilteredViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.RefreshingViewState;
import ru.aakumykov.me.sociocat.b_cards_list.enums.eCardsList_SortingMode;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_ItemClickListener;
import ru.aakumykov.me.sociocat.b_cards_list.interfaces.iCardsList_View;
import ru.aakumykov.me.sociocat.b_cards_list.list_items.Card_ListItem;
import ru.aakumykov.me.sociocat.b_cards_list.stubs.CardsList_ViewStub;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.CardsWithoutTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.b_cards_list.view_states.LoadingCards_ViewState;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;

public class CardsList_Presenter extends BasicMVPList_Presenter implements iCardsList_ItemClickListener {

    private static final String TAG = CardsList_Presenter.class.getSimpleName();
    private final CardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private String mTagFilter;
    private boolean mHasParent;


    public CardsList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();

        setNeutralViewState();

        String action = getActionFromIntent();
        mHasParent = Intent.ACTION_VIEW.equals(action);

        if (Constants.ACTION_SHOW_CARDS_WITH_TAG.equals(action))
            loadCardsWithTag(false);
        else
            loadCards();
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
        if (null == mTagFilter)
            loadCards();
        else
            loadCardsWithTag(true);
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
        if (null != mTagFilter)
            loadMoreCardsWithTag();
        else
            loadMoreCards();
    }

    @Override
    protected BasicMVPList_ItemsTextFilter getItemsTextFilter() {
        return new BasicMVPList_ItemsTextFilter();
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



    private String getActionFromIntent() {
        Intent intent = mPageView.getInputIntent();
        if (null != intent)
            return intent.getAction();
        return null;
    }

    private String getTagNameFromIntent() {
        Intent intent = mPageView.getInputIntent();
        if (null != intent)
            return intent.getStringExtra(Constants.TAG_NAME);
        return null;
    }


    private void loadCards() {

        setViewState(new LoadingCards_ViewState(mHasParent));

        mCardsSingleton.loadFirstPortion(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                setViewState(new CardsWithoutTag_ViewState(mHasParent));

                mListView.setList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));

//                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private void loadMoreCards() {

        showThrobberItem();

        BasicMVPList_DataItem tailDataItem = mListView.getLastDataItem();
        if (null == tailDataItem) {
            showNoMoreCards();
            return;
        }

        Card tailCard = (Card) tailDataItem.getPayload();

        mCardsSingleton.loadCardsAfter(tailCard, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                int position2scroll = mListView.getVisibleListSize();

                mListView.appendList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVPList_DataItem createDataItem(Object object) {
                        return new Card_ListItem((Card) object);
                    }
                }));

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


    private void loadCardsWithTag(boolean isOnRefreshing) {

        mTagFilter = getTagNameFromIntent();
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

                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.TAGS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private void loadMoreCardsWithTag() {

        showThrobberItem();

        BasicMVPList_DataItem lastDataItem = mListView.getLastDataItem();

        if (null == lastDataItem) {
            showNoMoreCards();
            return;
        }

        Card card = (Card) lastDataItem.getPayload();

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

                mListView.showLoadmoreItem();

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

    // TODO: перенести в Basic
    private void restoreFilterWidget(BasicMVPList_ItemsTextFilter itemsFilter) {

        if (itemsFilter instanceof BasicMVPList_ItemsTextFilter)
        {
            String filterTxt = itemsFilter.getFilterText();
            setViewState( new ListFilteredViewState(filterTxt) );
        }
    }
}