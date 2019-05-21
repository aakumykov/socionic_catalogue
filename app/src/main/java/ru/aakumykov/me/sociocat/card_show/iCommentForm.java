package ru.aakumykov.me.sociocat.card_show;

import android.content.Context;
import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iCommentForm {

    interface CommentFormCallbacks {
        void onSendCommentClicked(String commentText);
    }

    void attachTo(ViewGroup container);
    void setQuote(ListItem listItem);

    void show();
    void hide();

    void enable();
    void disable();
}
