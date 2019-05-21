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
        View formLayout = layoutInflater.inflate(R.layout.card_show_comment_form, null);
//        View formLayout = layoutInflater.inflate(R.layout.card_show_comment_form, container);
        container.removeAllViews();
        container.addView(formLayout);
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
