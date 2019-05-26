package ru.aakumykov.me.sociocat.utils.comment_form;

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

    void showError(int messageId, String consoleMessage);
    void hideError();
}
