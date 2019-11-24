package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCardShow2 {

    interface iPageView extends iBaseView {
    }

    interface iDataAdapter {
        void showCardThrobber();
        void showCommentsThrobber();

        void showCard(Card card);
        void appendComments(List<Comment> commentsList);

        Comment getLastComment();
    }

    interface iPresenter {
        void bindView(iPageView view);
        void unbindView();

        void bindDataAdapter(iDataAdapter dataAdapter);
        void unbindDataAdapter();

        void onPageOpened(String cardKey);
        void onLoadMoreClicked();
    }
}
