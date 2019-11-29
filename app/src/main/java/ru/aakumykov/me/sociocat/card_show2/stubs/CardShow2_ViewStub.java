package ru.aakumykov.me.sociocat.card_show2.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.models.Comment;

@SuppressLint("Registered")
public class CardShow2_ViewStub extends BaseView implements iCardShow2.iPageView {

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void showCommentForm(@Nullable Comment editedComment, @Nullable String quotedText) {

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
    public String getCommentText() {
        return null;
    }

}
