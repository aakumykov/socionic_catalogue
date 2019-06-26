package ru.aakumykov.me.sociocat.cards_grid;

import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsGrig {

    int MODE_ADMIN = 100;
    int MODE_OWNER = 20;
    int MODE_USER = 10;
    int MODE_GUEST = 0;

    interface iPageView extends iBaseView {
        <T> void setTitle(T title);

        void scrollToPosition(Integer position);

        void goShowCard(Card card);
        void goCreateCard(Constants.CardType cardType);
        void goEditCard(Card card, int position);
    }

    interface iGridView {
        void linkPresenter(iPresenter presenter);
        void unlinkPresenter();

        void setList(List<iGridItem> inputList);
        void restoreList(List<iGridItem> inputList, @Nullable Integer scrollToPosition);
        void appendList(List<iGridItem> inputList, boolean forceLoadMoreItem, @Nullable Integer scrollToPosition);

        iGridItem getGridItem(int position);
        iGridItem getLastContentItem();
        int getItemPosition(iGridItem item);

        iGridItem getItemBeforeLoadmore(int loadmorePosition);
        iGridItem getItemAfterLoadmore(int loadmorePosition);

        void addItem(Card card);
        void addItem(iGridItem gridItem);
        void updateItem(int position, iGridItem newGridItem);
        void removeItem(iGridItem gridItem);

        void hideLoadMoreItem(int position);

        void showThrobber();
        void showThrobber(int position);
        void hideThrobber();
        void hideThrobber(int position);

        void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder);
    }

    interface iPresenter {
        void linkViews(iPageView pageView, iGridView gridView);
        void unlinkViews();

        void onWorkBegins();
        void onLoadMoreClicked(int position);

        void onCardClicked(int position);
        void onCardLongClicked(int position, View view, iGridViewHolder gridViewHolder);

        void onCreateCardClicked(Constants.CardType cardType);
        void onEditCardClicked(iGridItem gridItem);
        void onDeleteCardClicked(iGridItem gridItem);
        void onShareCardClicked(iGridItem gridItem);
    }
}
