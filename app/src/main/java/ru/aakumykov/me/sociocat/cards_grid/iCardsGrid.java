package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsGrid {

    int MODE_ADMIN = 100;
    int MODE_OWNER = 20;
    int MODE_USER = 10;
    int MODE_GUEST = 0;


    interface iGridItemClickListener {
        void onGridItemClicked(View view);
        void onGridItemLongClicked(View view);
    }

    interface iLoadOldClickListener {
        void onLoadOldClicked(View view);
    }

    interface CheckNewCardsCallbacks {
        void onNewCardsChecked();
    }


    interface iPageView extends iBaseView {
        <T> void setTitle(T title);

        void goShowCard(Card card);
        void goCreateCard(Constants.CardType cardType);
        void goEditCard(Card card, int position);
        void goCardsGrid();

        String getCurrentFilterWord();
        String getCurrentFilterTag();

        void showTagFilter(String tagName);

        void storeAction(String action);

        void showToolbarThrobber();
        void hideToolbarThrobber();

        void showSwipeThrobber();
        void hideSwipeThrobber();

        void scroll2position(int position);
    }

    interface iGridView {
        void linkPresenter(iPresenter presenter);
        void unlinkPresenter();

        void setList(List<iGridItem> inputList);
        void insertList(int position, List<iGridItem> list);
        void insertItem(int position, iGridItem gridItem);

        void addList(List<iGridItem> inputList, int position, boolean forceLoadMoreItem, @Nullable Integer scrollToPosition);

        void restoreOriginalList();

        void addItem(iGridItem gridItem);
        void updateItem(int position, iGridItem newGridItem);
        void removeItem(iGridItem gridItem);

        iGridItem getGridItem(int position);
        iGridItem getGridItem(@NonNull Card searchedCard);

        int getItemPosition(iGridItem item);
        GridItem_Card getLastCardItem();

        List<iGridItem> getList();

        void showLoadOldItem();
        void hideLoadOldItem(int position);

        void showThrobber(int position);
        void hideThrobber(int position);

        void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder);

        void enableFiltering();
        void disableFiltering();
        boolean filterIsEnabled();

        void applyFilterToGrid(String filterKey);
    }

    interface iPresenter {
        void linkViews(iPageView pageView, iGridView gridView);
        void unlinkViews();

        void processInputIntent(@Nullable Intent intent);

        void onRefreshRequested();

        void onCheckNewCardsClicked();

        void onLoadOldClicked(int position);

        void onCardClicked(int position);
        void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder);

        void onCreateCardClicked(Constants.CardType cardType);
        void onEditCardClicked(iGridItem gridItem);
        void onDeleteCardClicked(iGridItem gridItem);
        void onShareCardClicked(iGridItem gridItem);

        List<iGridItem> filterList(List<iGridItem> inputList);
        void onFilteringTagDiscardClicked();
    }
}
