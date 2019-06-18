package ru.aakumykov.me.sociocat.cards_grid_3;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCG3 {

    interface iPageView extends iBaseView {
        <T> void setTitle(T title);
        void goShowCard(Card card);
    }

    interface iGridView {
        void linkPresenter(iPresenter presenter);
        void unlinkPresenter();

        void setItemsList(List<iGridItem> inputList);
        void addList(List<iGridItem> inputList);

        void addItem(iGridItem item);
        void removeItem(iGridItem item);
        void updateItem(iGridItem item);

        iGridItem getItem(int position);

        void hideLoadMoreItem(int position);

        void showThrobber();
        void showThrobber(int position);
        void hideThrobber();
        void hideThrobber(int position);
    }

    interface iPresenter {
        void linkViews(iPageView pageView, iGridView gridView);
        void unlinkViews();

        void onWorkBegins();
        void onLoadMoreClicked(int position, String startKey);

        void onCardClicked(int position);
    }
}
