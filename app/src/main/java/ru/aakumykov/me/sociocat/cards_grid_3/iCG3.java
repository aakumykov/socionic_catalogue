package ru.aakumykov.me.sociocat.cards_grid_3;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iCG3 {

    interface iPageView extends iBaseView {
        <T> void setTitle(T title);
    }

    interface iGridView {
        void setList(List<iGridItem> list);
        void appendList(List<iGridItem> list);

        void addItem(iGridItem item);
        void removeItem(iGridItem item);
        void updateItem(iGridItem item);
    }

    interface iPresenter {
        void linkView(iPageView pageView, iGridView gridView);
        void unlinkView();

        void onWorkBegins();
    }
}
