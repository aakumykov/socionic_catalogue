package ru.aakumykov.me.sociocat.card_show.comment_form;

import android.content.Context;
import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iCommentForm {

    interface SendButtonListener {
        void onSendCommentClicked(String commentText);
    }

    void attachTo(ViewGroup container);
    void addSendButtonListener(SendButtonListener listener);

    void setQuote(ListItem listItem);

    void show();
    void remove();

    void enable();
    void disable();
}
