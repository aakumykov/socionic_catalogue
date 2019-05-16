package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;

public interface iCardShow2_View extends iBaseView {

    void displayCard(Card card);
    void displayComments(List<Comment> list);

    void showCardThrobber();
    void hideCardThrobber();

    void showCommentsThrobber();
    void hideCommentsThrobber();

    void showCommentForm(Item parentItem);
    void showCommentForm(int commentPosition);
    void hideCommentForm();

    void enableCommentForm();
    void disableCommentForm();
}
