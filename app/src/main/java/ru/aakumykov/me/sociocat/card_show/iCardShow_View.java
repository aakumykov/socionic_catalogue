package ru.aakumykov.me.sociocat.card_show;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iCardShow_View {

    void showCommentForm(@Nullable String quotedText, ListItem parentItem);
    void hideCommentForm();

}
