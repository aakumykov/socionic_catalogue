package ru.aakumykov.me.sociocat.card_show2;

import android.annotation.SuppressLint;

import java.util.List;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

@SuppressLint("Registered")
public class CardShow2_ViewStub extends BaseView implements iCardShow2.iPageView {

    @Override
    public void displayCard(Card card) {

    }

    @Override
    public void displayComments(List<Comment> commentsList) {

    }

    @Override
    public Comment getLastComment() {
        return null;
    }

    @Override
    public void appendComments(List<Comment> list) {

    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
