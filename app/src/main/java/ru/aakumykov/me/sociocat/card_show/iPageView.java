package ru.aakumykov.me.sociocat.card_show;

import android.app.Activity;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iPageView extends iBaseView {

    Activity getActivity();

    void showCommentForm(iTextItem repliedItem);
    void hideCommentForm();
}
