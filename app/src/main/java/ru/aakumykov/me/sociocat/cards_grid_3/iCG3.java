package ru.aakumykov.me.sociocat.cards_grid_3;

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

        void setList(List<iGridItem> inputList);
        void appendList(List<iGridItem> inputList);

        void addItem(iGridItem item);
        void removeItem(iGridItem item);
        void updateItem(iGridItem item);

        iGridItem getItem(int position);

        void showLoadThrobber();
        void hideLoadThrobber();
    }

    interface iPresenter {
        void linkViews(iPageView pageView, iGridView gridView);
        void unlinkViews();

        void onWorkBegins();

        void onCardClicked(int position);
        void onLoadMoreClicked(int position, String startKey);
    }
}
