package ru.aakumykov.me.sociocat.card_show_2;

import android.content.Context;

import ru.aakumykov.me.sociocat.models.Item;

public interface iController {

    Context getContext();

    void showCommentForm(Item item);
    void hideCommentForm();

}
