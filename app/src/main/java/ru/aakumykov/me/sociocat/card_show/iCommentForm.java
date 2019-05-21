package ru.aakumykov.me.sociocat.card_show;

import android.content.Context;
import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iCommentForm {

    void attachTo(Context context, ViewGroup container);

    void show();
    void hide();

    void enable();
    void disable();

    String getText();

    void setQuote(ListItem listItem);
    void clearQuote();
}
