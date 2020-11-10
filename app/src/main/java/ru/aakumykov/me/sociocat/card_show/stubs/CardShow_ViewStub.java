package ru.aakumykov.me.sociocat.card_show.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;

@SuppressLint("Registered")
public class CardShow_ViewStub extends BaseView_Stub implements iCardShow.iPageView {

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
    public void goEditCard(Card card) {

    }

    @Override
    public void goUserProfile(String userId) {

    }

    @Override
    public void openImageInBrowser(String imageURL) {

    }

    @Override
    public void goBack(@NonNull Card card) {

    }

    @Override
    public void refreshMenu() {

    }

    @Override
    public String getCommentText() {
        return null;
    }

}
