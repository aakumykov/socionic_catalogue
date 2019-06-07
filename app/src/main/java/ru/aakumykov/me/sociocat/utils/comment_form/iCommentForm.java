package ru.aakumykov.me.sociocat.utils.comment_form;

import android.view.ViewGroup;

public interface iCommentForm {

    interface ButtonListeners {
        void onClearQuoteClicked();
        void onSendCommentClicked(String commentText);
    }

    void attachTo(ViewGroup container);
    void addButtonListeners(ButtonListeners listeners);

    void setQuote(String text);

    void setText(String text);
    String getText();

    void clear();

    void show(boolean isEditMode);
    void hide();

    void enable();
    void disable();

    boolean isVisible();
    boolean isEmpty();

    void showError(int messageId, String consoleMessage);
    void hideError();
}
