package ru.aakumykov.me.sociocat.card_show;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;

public interface iCardShow_View extends iBaseView {

    void showCommentForm(Item parentItem);
    void hideCommentForm();

    void enableCommentForm();
    void disableCommentForm();
}
