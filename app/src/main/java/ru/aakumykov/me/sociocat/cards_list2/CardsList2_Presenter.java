package ru.aakumykov.me.sociocat.cards_list2;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
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
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.tags_list.TagsList_DataAdapter;

public class CardsList2_Presenter extends BasicMVP_Presenter implements iCardsList2_ItemClickListener {

    private final CardsSingleton mCardsSingleton = CardsSingleton.getInstance();


    public CardsList2_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    public void onCardEdited(@Nullable Card oldCard, @Nullable Card newCard) {
        if (null != oldCard && null != newCard) {
            int position = ((CardsList2_DataAdapter) mListView).updateCardInList(oldCard, newCard);
            mPageView.scroll2position(position);
            mListView.highlightItem(position);
        }
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();
        loadCardsFromBeginning();
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
        loadCardsFromBeginning();
    }

    @Override
    public void onItemClicked(BasicMVP_DataViewHolder dataViewHolder) {

        if (mListView.isSelectionMode()) {
            onSelectItemClicked(dataViewHolder);
            return;
        }

        Card card = getCorrespondingCard(dataViewHolder);
        ((iCardsList2_View) mPageView).goShowingCard(card);
    }

    @Override
    public void onItemLongClicked(BasicMVP_DataViewHolder dataViewHolder) {
        onSelectItemClicked(dataViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder) {

        mListView.hideLoadmoreItem();
        mListView.showThrobberItem();

        BasicMVP_DataItem lastDataItem = mListView.getLastUnfilteredDataItem();
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


    private void loadCardsFromBeginning() {
        setRefreshingViewState();

        mCardsSingleton.loadFirstPortion(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                setNeutralViewState();

                mListView.setList(
                        ListUtils.incapsulateObjects2basicItemsList(list, new ListUtils.iIncapsulationCallback() {
                        @Override
                        public BasicMVP_DataItem createDataItem(Object payload) {
                            return new Card_ListItem((Card) payload);
                        }
                    })
                );

                mListView.showLoadmoreItem();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                setErrorViewState(R.string.CARDS_LIST_error_loading_list, errorMessage);
            }
        });
    }

    private Card getCorrespondingCard(BasicMVP_DataViewHolder basicDataViewHolder) {
        int index = basicDataViewHolder.getAdapterPosition();
        BasicMVP_ListItem listItem = mListView.getItem(index);
        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) listItem;
        return (Card) dataItem.getPayload();
    }

}
