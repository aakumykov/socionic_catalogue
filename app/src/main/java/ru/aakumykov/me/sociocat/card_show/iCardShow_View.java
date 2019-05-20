package ru.aakumykov.me.sociocat.card_show;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.ListItem;

public interface iCardShow_View extends iBaseView {

    void showCommentForm(ListItem parentItem);
    void hideCommentForm();

    void enableCommentForm();
    void disableCommentForm();
}
