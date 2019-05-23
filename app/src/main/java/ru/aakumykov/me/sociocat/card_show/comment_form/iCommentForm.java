package ru.aakumykov.me.sociocat.card_show.comment_form;

import android.view.ViewGroup;

public interface iCommentForm {

    interface SendButtonListener {
        void onSendCommentClicked(String commentText);
    }

    void attachTo(ViewGroup container);
    void addSendButtonListener(SendButtonListener listener);

    void setQuote(String text);

    void show();
    void hide();

    void enable();
    void disable();

    boolean isVisible();
}
