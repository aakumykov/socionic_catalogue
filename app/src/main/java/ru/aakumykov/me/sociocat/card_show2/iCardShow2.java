package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCardShow2 {

    interface iPageView extends iBaseView {
        void displayCard(Card card);
        void displayComments(List<Comment> commentsList);

        Comment getLastComment();

        void appendComments(List<Comment> list);

        void showCommentsThrobber();
    }

    interface iDataAdapter {
        void setCard(Card card);
        void setComments(List<Comment> commentsList);
        void appendComments(List<Comment> commentsList);

        Comment getLastComment();

        void showCommentsThrobber();
    }

    interface iPresenter {
        void bindView(iPageView view);
        void unbindView();

        void onCardLoaded(Card card);
        void onLoadMoreClicked();
    }
}
