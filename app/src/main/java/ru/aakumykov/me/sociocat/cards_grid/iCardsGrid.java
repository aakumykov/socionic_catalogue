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
import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.services.NewCardsService;

public interface iCardsGrid {

    int MODE_ADMIN = 100;
    int MODE_OWNER = 20;
    int MODE_USER = 10;
    int MODE_GUEST = 0;


    interface iPageView extends iBaseView {
        <T> void setTitle(T title);

        void goShowCard(Card card, int position);
        void goCreateCard(Constants.CardType cardType);
        void goEditCard(Card card, int position);
        void goCardsGrid();

        String getCurrentFilterWord();
        String getCurrentFilterTag();

        void showTagFilter(String tagName);

        void showToolbarThrobber();
        void hideToolbarThrobber();

        void showSwipeThrobber();
        void hideSwipeThrobber();

        void scroll2position(int position);

        void showNewCardsNotification(int count);
        void hideNewCardsNotification();

        void showLoadingNewCardsThrobber();
        void hideLoadingNewCardsThrobber();

        void resetNewCardsCounter();
    }

    interface iDataAdapter {
        void linkPresenter(iPresenter presenter);
        void unlinkPresenter();

        void setList(List<iGridItem> inputList);
        void insertList(int position, List<iGridItem> list);
        void insertItem(int position, iGridItem gridItem);

        void addList(List<iGridItem> inputList, int position, boolean forceLoadMoreItem, @Nullable Integer scrollToPosition);

        void restoreOriginalList();

        void removeItem(iGridItem gridItem);
        void removeItem(int position);
        void updateItem(int position, Card card);

        iGridItem getGridItem(int position);
        iGridItem getGridItem(@NonNull Card searchedCard);

        int getItemPosition(iGridItem item);
        GridItem_Card getLastCardItem();
        GridItem_Card getFirstCardItem();

        List<iGridItem> getList();

        void showLoadMoreItem();
        void hideLoadMoreItem(int position);

        void addNewCards(List<iGridItem> gridItemsList, @Nullable Card newCardsBoundaryKey);

        void showThrobber(int position);
        void hideThrobber(int position);

        void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder);

        void enableFiltering();
        void disableFiltering();
        boolean filterIsEnabled();

        void applyFilterToGrid(String filterKey);

        boolean hasData();
    }

    interface iPresenter {
        void bindComponents(iPageView pageView, iDataAdapter dataAdapter);
        void unbindComponents();

        void bindNewCardsService(NewCardsService newCardsService);
        void unbindNewCardsService();

        void processInputIntent(@Nullable Intent intent);

        void onRefreshRequested();

        void onLoadMoreClicked(int position);

        void onNewCardsAvailable(int count);
        void onNewCardsAvailableClicked();

        void onCardClicked(int position);
        void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder);

        void onCreateCardClicked(Constants.CardType cardType);
        void onEditCardClicked(iGridItem gridItem);
        void onDeleteCardClicked(iGridItem gridItem);
        void onShareCardClicked(iGridItem gridItem);

        void onFilteringTagDiscardClicked();

        void processCardCreationResult(@Nullable Intent data);
    }


    interface iGridItemClickListener {
        void onGridItemClicked(View view);
        void onGridItemLongClicked(View view);
    }

    interface iLoadMoreClickListener {
        void onLoadMoreClicked(View view);
    }

}
