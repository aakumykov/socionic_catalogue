package ru.aakumykov.me.sociocat.card_show;

import android.app.Activity;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iPageView extends iBaseView {

    Activity getActivity();

    void showCommentForm(ListItem repliedItem);
    void hideCommentForm();
}
