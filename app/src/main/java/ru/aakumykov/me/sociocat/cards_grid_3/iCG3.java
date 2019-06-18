package ru.aakumykov.me.sociocat.cards_grid_3;

import android.view.View;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCG3 {

    int MODE_ADMIN = 100;
    int MODE_OWNER = 20;
    int MODE_USER = 10;
    int MODE_GUEST = 0;

    interface iPageView extends iBaseView {
        <T> void setTitle(T title);
        void goShowCard(Card card);
        void goEditCard(Card card);
    }

    interface iGridView {
        void linkPresenter(iPresenter presenter);
        void unlinkPresenter();

        void setList(List<iGridItem> inputList);
        void appendList(List<iGridItem> inputList);

        iGridItem getItem(int position);

        void hideLoadMoreItem(int position);

        void showThrobber();
        void showThrobber(int position);
        void hideThrobber();
        void hideThrobber(int position);

        void showPopupMenu(int mode, View view, int position);
    }

    interface iPresenter {
        void linkViews(iPageView pageView, iGridView gridView);
        void unlinkViews();

        void onWorkBegins();
        void onLoadMoreClicked(int position, String startKey);

        void onCardClicked(int position);
        void onCardLongClicked(View view, int position);

        void onEditClicked(iGridItem gridItem);
        void onDeleteClicked(iGridItem gridItem);
        void onShareClicked(iGridItem gridItem);
    }
}
