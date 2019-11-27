package ru.aakumykov.me.sociocat.card_show2;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCardShow2 {

    interface iPageView extends iBaseView {
        void showCommentForm();
        void hideCommentForm();

        void disableCommentForm();

        String getCommentText();
        void clearCommentForm();

        void showCommentFormError(int errorMessageId, String errorMsg);
    }

    interface iDataAdapter
    {
        void showCardThrobber();
        void showCommentsThrobber(@Nullable Integer position);

        void showCard(Card card);

        void appendComments(List<Comment> commentsList);
        void insertComments(List<Comment> commentsList, int position);

        void appendOneComment(Comment comment);
        void removeComment(iList_Item listItem);

        Comment getComment(int position);
        Comment getComment(iList_Item listItem);
    }

    interface iPresenter {
        void bindView(iPageView view);
        void unbindView();

        void bindDataAdapter(iDataAdapter dataAdapter);
        void unbindDataAdapter();

        void onPageOpened(String cardKey);

        void onLoadMoreClicked(int position);

        void onAddCommentClicked(iList_Item listItem);

        void onSendCommentClicked();

        void onDeleteCommentClicked(iList_Item listItem, iCommentViewHolder commentViewHolder);
    }

    interface iCommentViewHolder {
        void fadeBackground();
        void unfadeBackground();
    }
}
