package ru.aakumykov.me.sociocat.card_show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public class CommentForm implements iCommentForm {

    @Override
    public void attachTo(Context context, ViewGroup container) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        container.removeAllViews();
        layoutInflater.inflate(R.layout.card_show_comment_form, container, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void setParent(ListItem listItem) {

    }

    @Override
    public void clearParent() {

    }

    @Override
    public String getText() {
        return null;
    }
}
