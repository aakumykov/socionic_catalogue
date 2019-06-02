package ru.aakumykov.me.sociocat.card_show;

import android.app.Activity;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iPageView extends iBaseView {

    Activity getActivity();

    void showCommentForm(@Nullable String initialText, @Nullable String quote);
    void hideCommentForm();
}
