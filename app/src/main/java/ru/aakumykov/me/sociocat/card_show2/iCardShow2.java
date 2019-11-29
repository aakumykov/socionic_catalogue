package ru.aakumykov.me.sociocat.card_show2;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;

public interface iCardShow2
{
    String REPLY_ACTION = "REPLY_ACTION";
    String ACTION_REPLY_TO_CARD = "ACTION_REPLY_TO_CARD";
    String ACTION_REPLY_TO_COMMENT = "ACTION_REPLY_TO_COMMENT";
    String REPLIED_OBJECT = "REPLIED_OBJECT";

    interface iPageView extends iBaseView {
        void showCommentForm(@Nullable Comment editedComment, @Nullable String quotedText);
        void hideCommentForm();

        void disableCommentForm();

        String getCommentText();

        void clearCommentForm();

        void showCommentFormError(int errorMessageId, String errorMsg);

        void scrollToComment(int position);
    }

    interface iDataAdapter {
        void showCardThrobber();
        void showCommentsThrobber(@Nullable Integer position);

        void showCard(Card card);

        void appendComments(List<Comment> commentsList);
        void insertComments(List<Comment> commentsList, int position);

        int appendOneComment(Comment comment);
        void removeComment(iList_Item listItem);
        void updateComment(iList_Item listItem, Comment newComment);

        Comment getComment(int position);
        Comment getComment(iList_Item listItem);

        int getIndexOf(iList_Item listItem);
    }

    interface iPresenter {
        void bindViewAndAdapter(iPageView pageView, iDataAdapter dataAdapter);
        void unbindViewAndAdapter();

        void onPageOpened(String cardKey);

        void onLoadMoreClicked(iList_Item listItem);

        void onReplyClicked(iList_Item listItem);

        void onDeleteCommentClicked(iList_Item listItem, iCommentViewHolder commentViewHolder);
        void onDeleteCommentConfirmed(iList_Item listItem, iCardShow2.iCommentViewHolder commentViewHolder);

        void onEditCommentClicked(iList_Item listItem);

        void onRemoveCommentQuoteClicked();
        void onSendCommentClicked();

        boolean canEditCard();
        boolean canDeleteCard();

        void processLoginRequest(String replyAction, iCommentable repliedObject);
    }

    interface iCommentViewHolder {
        void fadeBackground();
        void unfadeBackground();
    }
}
