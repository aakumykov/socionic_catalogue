package ru.aakumykov.me.sociocat.cards_list2;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ListUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.TextUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_ViewHolder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_ItemClickListener;
import ru.aakumykov.me.sociocat.cards_list2.interfaces.iCardsList2_View;
import ru.aakumykov.me.sociocat.cards_list2.list_items.Card_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.stubs.CardsList2_ViewStub;
import ru.aakumykov.me.sociocat.cards_list2.view_states.CardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.CardsWithoutTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.LoadingCardsWithTag_ViewState;
import ru.aakumykov.me.sociocat.cards_list2.view_states.LoadingCards_ViewState;
import ru.aakumykov.me.sociocat.eCardType;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;

public class CardsList2_Presenter extends BasicMVP_Presenter implements iCardsList2_ItemClickListener {

    private static final String TAG = CardsList2_Presenter.class.getSimpleName();
    private final CardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private String mTagFilter;
    private boolean mHasParent;


    public CardsList2_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        Log.d("onColdStart", "onColdStart()");
        super.onColdStart();

        String action = getActionFromIntent();

        mHasParent = Intent.ACTION_VIEW.equals(action);

        if (Constants.ACTION_SHOW_CARDS_WITH_TAG.equals(action))
            loadCardsWithTag();
        else
            loadCards();
    }

    @Override
    public void unbindViews() {
        mPageView = new CardsList2_ViewStub();
    }

    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {
        return eSortingOrder.DIRECT;
    }

    @Override
    protected void onRefreshRequested() {
        if (null == mTagFilter)
            loadCards();
        else
            loadCardsWithTag();
    }

    @Override
    public void onItemClicked(BasicMVP_DataViewHolder dataViewHolder) {

        if (mListView.isSelectionMode()) {
            onSelectItemClicked(dataViewHolder);
            return;
        }

        Card card = getCardForViewHolder(dataViewHolder);
        ((iCardsList2_View) mPageView).goShowingCard(card);
    }

    @Override
    public void onItemLongClicked(BasicMVP_DataViewHolder dataViewHolder) {
        onSelectItemClicked(dataViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder) {
        if (null != mTagFilter)
            loadMoreCardsWithTag();
        else
            loadMoreCards();
    }



    public void onCardEdited(@Nullable Card oldCard, @Nullable Card newCard) {

        if (null != oldCard && null != newCard)
        {
            Card_ListItem newCardListItem = new Card_ListItem(newCard);

            int position = mListView.updateItemInList(newCardListItem, new iBasicList.iFindItemComparisionCallback() {
                @Override
                public boolean onCompareFindingOldItemPosition(Object objectFromListItem) {
                    Card cardFromList = (Card) objectFromListItem;
                    return cardFromList.getKey().equals(oldCard.getKey());
                }
            });

            mPageView.scroll2position(position);
            mListView.highlightItem(position);
        }
    }

    public void onFABClicked() {
        ((CardsList2_View) mPageView).showAddNewCardMenu();
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

        ((CardsList2_View) mPageView).goCreateCard(cardType);
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
        ((iCardsList2_View) mPageView).goShowAllCards();
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
                            public BasicMVP_DataItem createDataItem(Object payload) {
                                return new Card_ListItem((Card) payload);
                            }
                        }));
                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private void loadMoreCards() {

        showThrobberItem();

        BasicMVP_DataItem lastDataItem = mListView.getLastUnfilteredDataItem();
        if (null == lastDataItem) {
            showNoMoreCards();
            return;
        }

        Card card = (Card) lastDataItem.getPayload();

        mCardsSingleton.loadCardsAfter(card, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                mListView.hideThrobberItem();

                List<BasicMVP_ListItem> list2append = ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVP_DataItem createDataItem(Object object) {
                        return new Card_ListItem((Card) object);
                    }
                });

                if (mListView.isFiltered()) {
                    mListView.appendListAndFilter(list2append);
                }
                else {
                    String msg = TextUtils.getPluralString(mPageView.getAppContext(), R.plurals.CARDS_LIST_n_cards_mode_loaded, list.size());
                    mPageView.showToast(msg);

                    mListView.appendList(list2append);
                }

                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                mPageView.showToast(R.string.CARDS_LIST_error_loading_list);
                mListView.hideThrobberItem();
                mListView.showLoadmoreItem();
            }
        });
    }


    private void loadCardsWithTag() {

        mTagFilter = getTagNameFromIntent();
        if (null == mTagFilter) {
            setErrorViewState(R.string.CARDS_LIST_error_tag_name_missing, "Нет имени метки");
            return;
        }

        setViewState(new LoadingCardsWithTag_ViewState(mTagFilter));

        mCardsSingleton.loadCardsWithTag(mTagFilter, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                String msg = mPageView.getText(R.string.CARDS_LIST_cards_with_tag, mTagFilter);
                setViewState(new CardsWithTag_ViewState(msg));

                mListView.setList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVP_DataItem createDataItem(Object payload) {
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

        BasicMVP_DataItem lastDataItem = mListView.getLastDataItem();

        if (null == lastDataItem) {
            showNoMoreCards();
            return;
        }

        Card card = (Card) lastDataItem.getPayload();

        mCardsSingleton.loadCardsWithTagAfter(mTagFilter, card, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                String msg = mPageView.getText(R.string.CARDS_LIST_cards_with_tag, mTagFilter);
                setViewState(new CardsWithTag_ViewState(msg));

                if (0 == list.size()) {
                    showNoMoreCards();
                    return;
                }

                mListView.appendList(ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                    @Override
                    public BasicMVP_DataItem createDataItem(Object payload) {
                        return new Card_ListItem((Card) payload);
                    }
                }));

                mListView.hideThrobberItem();
                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }


    private Card getCardForViewHolder(BasicMVP_DataViewHolder basicDataViewHolder) {
        int index = basicDataViewHolder.getAdapterPosition();
        BasicMVP_ListItem listItem = mListView.getItem(index);
        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) listItem;
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
}
