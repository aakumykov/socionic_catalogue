package ru.aakumykov.me.sociocat.card_show.stubs;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;

@SuppressLint("Registered")
public class CardShow_ViewStub extends BaseView implements iCardShow.iPageView {

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void hideSwipeThrobber() {

    }

    @Override
    public void showCommentForm(Comment editedComment) {

    }

    @Override
    public void showCommentForm(iCommentable repliedItem) {

    }

    @Override
    public void hideCommentForm() {

    }

    @Override
    public void disableCommentForm() {

    }

    @Override
    public void clearCommentForm() {

    }

    @Override
    public void showCommentFormError(int errorMessageId, String errorMsg) {

    }

    @Override
    public void scrollToComment(int position) {

    }

    @Override
    public void showCardsWithTag(String tagName) {

    }

    @Override
    public void refreshMenu() {

    }

    @Override
    public String getCommentText() {
        return null;
    }

}