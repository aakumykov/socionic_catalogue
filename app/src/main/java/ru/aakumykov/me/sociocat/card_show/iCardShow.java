package ru.aakumykov.me.sociocat.card_show;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public interface iCardShow {

    // Страница
    interface iPageView extends iBaseView {

        void refreshMenu();

        void scrollListToPosition(int position);

        void goEditCard(Card card);
        void goShowCardsWithTag(String tagName);

        void showCommentForm(iTextItem repliedItem, boolean editMode);
        void hideCommentForm(boolean withQuestion);
    }


    // Адаптер данных
    interface iDataAdapter {

        void bindPresenters(iCardPresenter cardPresenter, iCommentsPresenter commentPresenter);
        void unbindPresenters();

        void bindView(iPageView pageView);
        void unbindView();
    }


    // Отображение карточки
    interface iCardView {

        void displayCard(@Nullable Card card) throws Exception;

        void showCardThrobber();
        void hideCardThrobber();

        void showCardError(int errorMsgId, String errorMsg);
        void hideCardError();

        void showCardDeleteDialog(Card card);
    }

    // Отображение комментариев
    interface iCommentsView {

        interface AttachCommentCallbacks {
            void onCommentAttached(Comment comment);
        }

        void showCommentsThrobber(int position);
        void hideCommentsThrobber(int position);

        void showCommentsError(int errorMsgId, String consoleErrorMsg);
        void hideCommentsError();

        void showDeleteDialog(Comment comment);

        void setList(List<Comment> itemsList);
        void addList(List<Comment> list, int position, @Nullable Comment alreadyVisibleTailComment);

        void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks);
        void updateComment(Comment oldComment, Comment newComment);
        void removeComment(Comment comment);

        Comment getComment(String commentKey);

        void scrollToComment(String commentKey);
    }


    // "Презенторы"
    interface iCardPresenter {

        void bindPageView(iPageView pageView);
        void unbindPageView();

        void bindListAdapter(iCardView listAdapter);
        void unbindListAdapter();

        void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
        void onErrorOccurs();

        void onReplyClicked();

        Card getCard();

        boolean canEditCard();
        boolean canDeleteCard();

        void onEditClicked();
        void onCardEdited(Card card);

        void onDeleteClicked();
        void onDeleteConfirmed();

        void onTagClicked(String tag);

        void onRatingUpClicked(iCard_ViewHolder cardViewHolder);
        void onRatingDownClicked(iCard_ViewHolder cardViewHolder);
    }

    interface iCommentsPresenter {

        void bindPageView(iPageView pageView);
        void unbindPageView();

        void bindCommentsView(iCommentsView listAdapter);
        void unbindCommentsView();


        void onWorkBegins(String cardKey, @Nullable String scrollToCommentKey);

        void onLoadMoreClicked(int insertPosition, @Nullable Comment beginningComment);


        void onReplyClicked(iTextItem repliedItem);

        void onEditCommentClicked(Comment comment);

        void onDeleteCommentClicked(Comment comment);

        void onDeleteConfirmed(Comment comment);

        void onSendCommentClicked(iCommentForm commentForm);
    }


    // ViewHolder-ы
    interface iCard_ViewHolder {

        void showRatingThrobber();
        void hideRatingThrobber();

        void disableRatingButtons();
        void enableRatingContols();

        void setRatedUp(Card card, String ratedByUserId);
        void setRatedDown(Card card, String ratedByUserId);

    }
}
