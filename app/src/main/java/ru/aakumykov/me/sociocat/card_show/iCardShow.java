package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.iCard_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;

public interface iCardShow
{
    String REPLY_ACTION = "REPLY_ACTION";
    String ACTION_REPLY_TO_CARD = "ACTION_REPLY_TO_CARD";
    String ACTION_REPLY_TO_COMMENT = "ACTION_REPLY_TO_COMMENT";
    String REPLIED_OBJECT = "REPLIED_OBJECT";

    interface iPageView extends iBaseView {
        void showCommentForm(Comment editedComment);
        void showCommentForm(iCommentable repliedItem);
        void hideCommentForm();
        void disableCommentForm();
        String getCommentText();
        void clearCommentForm();
        void showCommentFormError(int errorMessageId, String errorMsg);

        void scrollToComment(int position);

        void showCardsWithTag(String tagName);

        void goEditCard(Card card);
        void goUserProfile(String userId);

        void openImageInBrowser(String imageURL);

        void goBack(@NonNull Card card);

        void showRefreshThrobber();
        void hideRefreshThrobber();
    }

    interface iDataAdapter {
        boolean notYetFilled();

        void showCardThrobber();

        void showCard(@NonNull Card card);

        int appendOneComment(Comment comment);
        void removeComment(iList_Item listItem);

        void updateComment(iList_Item listItem, Comment newComment);

        Comment getComment(int position);
        Comment getComment(iList_Item listItem);

        int getIndexOf(iList_Item listItem);

        // Новые (улучшенные) методы работы по списком
        void addCommentsList(List<Comment> list);
        void addCommentsList(List<Comment> list, int position);

        void replaceComments(List<Comment> list);

        void showCommentsThrobber2();
        void showCommentsThrobber2(int position);
        void hideCommentsThrobber2();
        void hideCommentsThrobber2(int position);

        void clearCommentsList();

        int getCommentPositionByKey(@NonNull String commentKey);

        void highlightComment(int position);
    }

    interface iPresenter {
        void bindViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unbindViewAndAdapter();

        void onFirstOpen(@Nullable Intent data);

        boolean canEditCard();
        boolean canDeleteCard();

        void onRefreshRequested();

        void onCardAlmostDisplayed(Card_ViewHolder cardViewHolder);

        void processLoginRequest(Intent transitIntent) throws IllegalArgumentException;

        void onAuthorClicked();
        void onTagClicked(String tagName);
        void onReplyClicked(iList_Item listItem);
        void onCardRateUpClicked(iCard_ViewHolder cardViewHolder);
        void onCardRateDownClicked(iCard_ViewHolder cardViewHolder);
        void onEditCardClicked();
        void onEditCardComplete(@Nullable Intent data);
        void onDeleteCardClicked();

        void onLoadMoreClicked(iList_Item listItem);

        void onDeleteCommentClicked(iList_Item listItem, iComment_ViewHolder commentViewHolder);
        void onDeleteCommentConfirmed(iList_Item listItem, iComment_ViewHolder commentViewHolder);
        void onEditCommentClicked(iList_Item listItem);
        void onRemoveCommentQuoteClicked();
        void onSendCommentClicked();
        void onCommentAuthorClicked(iList_Item commentItem);
        void onCommentRateUpClicked(iComment_ViewHolder commentViewHolder, iList_Item commentItem);
        void onCommentRateDownClicked(iComment_ViewHolder commentViewHolder, iList_Item commentItem);

        void onOpenInBrowserClicked();

        void onGoBackRequested();
    }

}
