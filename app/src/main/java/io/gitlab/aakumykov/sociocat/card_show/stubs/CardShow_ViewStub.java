package io.gitlab.aakumykov.sociocat.card_show.stubs;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.card_show.iCardShow;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.Comment;
import io.gitlab.aakumykov.sociocat.models.iCommentable;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView_Stub;

@SuppressLint("Registered")
public class CardShow_ViewStub extends BaseView_Stub implements iCardShow.iPageView {

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
    public void goShowCardsWithTag(String tagName) {

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
    public void goBack(@NonNull Card currentCard, @NonNull Card oldCard) {

    }

    @Override
    public void showRefreshThrobber() {

    }

    @Override
    public void hideRefreshThrobber() {

    }

    @Override
    public void closeAfterCardDeleted(@NonNull Card currentCard) {

    }

    @Override
    public boolean isCommentFormDisabled() {
        return false;
    }

    @Override
    public void openURI(@NonNull Uri uri) {

    }

    @Override
    public void refreshMenu() {

    }

    @Override
    public String getCommentText() {
        return null;
    }

}
