package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;

public interface iCardShow2_View {

    void displayCard(Card card);
    void displayComments(List<Comment> list);

    void showCardThrobber();
    void hideCardThrobber();

    void showCommentsThrobber();
    void hideCommentsThrobber();

    void showCommentForm(Item parentItem);
    void hideCommentForm();

    void enableCommentForm();
    void disableCommentForm();
}
