package ru.aakumykov.me.sociocat.card_show;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iReplyView extends iBaseView {

    void showCommentForm(ListItem repliedItem);
    void hideCommentForm();

    void enableCommentForm();
    void disableCommentForm();
}
