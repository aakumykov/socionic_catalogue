package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.comment.iComments;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.CommentsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;


public class CardShow_Presenter implements
        iCardShow.Presenter,
        iCardsSingleton.LoadCallbacks,
        iCardsSingleton.DeleteCallbacks,
        iCommentsSingleton.CreateCallbacks,
        iCommentsSingleton.ListCallbacks,
        iCommentsSingleton.DeleteCallbacks
{

    private final static String TAG = "CardShow_Presenter";
    private iCardShow.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iCommentsSingleton commentsService = CommentsSingleton.getInstance();
    private Card currentCard;


    // Получение карточки
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {

        if (null == intent) throw new IllegalArgumentException("intent == null");

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey) throw new Exception("Intent has no CARD_KEY");

        view.showProgressBar();
        cardsService.loadCard(cardKey, this);
    }

    @Override
    public void loadComments(Card card) {
        if (card.getCommentsCount() > 10) view.showCommentsThrobber();
        commentsService.loadList(card.getKey(), this);
    }

    // Изменение рейтинга
    @Override
    public void rateCardUp() {
        if (authService.isUserLoggedIn()) {

            view.showCardRatingThrobber();

            cardsService.rateUp(currentCard.getKey(), authService.currentUserId(), new iCardsSingleton.RatingCallbacks() {
                @Override
                public void onRetedUp(int newRating) {
                    view.onCardRatedUp(newRating);
                }

                @Override
                public void onRatedDown(int newRating) {
                    // не используется
                }

                @Override
                public void onRateFail(String errorMsg) {
                    view.onCardRateError();
                    Log.e(TAG, errorMsg);
                }
            });
        }
    }

    @Override
    public void rateCardDown() {
        if (authService.isUserLoggedIn()) {

            view.showCardRatingThrobber();

            cardsService.rateDown(currentCard.getKey(), authService.currentUserId(), new iCardsSingleton.RatingCallbacks() {
                @Override
                public void onRetedUp(int newRating) {
                    // не используется
                }

                @Override
                public void onRatedDown(int newRating) {
                    view.onCardRatedDown(newRating);
                }

                @Override
                public void onRateFail(String errorMsg) {
                    view.onCardRateError();
                    Log.e(TAG, errorMsg);
                }
            });
        }
    }


    // Добавление комментария
    @Override
    public void postComment(String text) {
        Comment comment = new Comment(
                text,
                currentCard.getKey(),
                null,
                null,
                authService.currentUserId(),
                authService.currentUserName(),
                authService.currentUser().getAvatarURL()
        );
        postComment(comment);
    }

    @Override
    public void postCommentReply(String replyText, final Comment parentComment) {
        Comment comment = new Comment(
                replyText,
                currentCard.getKey(),
                parentComment.getKey(),
                parentComment.getText(),
                authService.currentUserId(),
                authService.currentUserName(),
                authService.currentUser().getAvatarURL()
        );
        postComment(comment);
    }


    // Удаление комментария
    @Override
    public void deleteCommentConfirmed(Comment comment) {
        // TODO: переделать проверку по-правильному
        if (!authService.isUserLoggedIn())
            return;

        // TODO: эта проверка без проверки на залогиненность...
        if (!authService.currentUserId().equals(comment.getUserId())) {
            view.showErrorMsg(R.string.action_denied);
            return;
        }

        try {
            commentsService.deleteComment(comment, this);
        } catch (Exception e) {
            view.showErrorMsg(R.string.COMMENT_delete_error);
            e.printStackTrace();
        }
    }


    // Изменение комментария
    @Override
    public void editCommentConfirmed(final Comment comment) {
        // TODO: контроль длины

        if (!authService.isUserLoggedIn())
            return;

        if (!authService.currentUserId().equals(comment.getUserId())) {
            view.showErrorMsg(R.string.action_denied);
        }

        if (!TextUtils.isEmpty(comment.getText())) {
            try {
                view.showCommentInProgress();
                commentsService.updateComment(comment, new iCommentsSingleton.CreateCallbacks() {
                    @Override
                    public void onCommentSaveSuccess(Comment comment) {
//                            view
                    }

                    @Override
                    public void onCommentSaveError(String errorMsg) {
                        view.showErrorMsg(R.string.COMMENT_save_error);
                    }
                });

            } catch (Exception e) {
                view.showErrorMsg(R.string.COMMENT_save_error);
                e.printStackTrace();
            }
        }
    }


    // Оценка комментария


    @Override
    public void rateCommentUp(Comment comment, iCommentsSingleton.RatingCallbacks callbacks) {
        if (authService.isUserLoggedIn()) {
            commentsService.rateUp(comment.getKey(), authService.currentUserId(), callbacks);
        }
    }

    @Override
    public void rateCommentDown(Comment comment, iCommentsSingleton.RatingCallbacks callbacks) {
        if (authService.isUserLoggedIn()) {
            commentsService.rateDown(comment.getKey(), authService.currentUserId(), callbacks);
        }
    }

    // Реакция на кнопки
    @Override
    public void onTagClicked(String tagName) {
        view.goList(tagName);
    }

    @Override
    public void cardDeleteConfirmed(Card card) {
        view.showProgressBar();
        view.showInfoMsg(R.string.deleting_card);

        if (!authService.isUserLoggedIn()) return;

        // TODO: "или Админ"
        if (!authService.currentUserId().equals(card.getUserId())) {
            view.showErrorMsg(R.string.action_denied);
            return;
        }

        try {
            cardsService.deleteCard(currentCard, this);
        } catch (Exception e) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card);
            e.printStackTrace();
        }
    }


    // Link / Unlink
    @Override
    public void linkView(iCardShow.View view) {
        Log.d(TAG, "linkView(), view: "+view);
        this.view = view;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }


    // Коллбеки
    @Override
    public void onCardLoadSuccess(Card card) {
        this.currentCard = card;
        view.displayCard(card);

        loadComments(card);
    }

    @Override
    public void onCardLoadFailed(String msg) {
        this.currentCard = null;
        view.showErrorMsg(R.string.card_load_error);
    }

    @Override
    public void onCardDeleteSuccess(Card card) {
        TagsSingleton.getInstance().updateCardTags(
                card.getKey(),
                card.getTags(),
                null,
                null
        );

        try {
            commentsService.deleteCommentsForCard(currentCard.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.closePage();
    }

    @Override
    public void onCardDeleteError(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card);
    }


    @Override
    public void onCommentSaveSuccess(Comment comment) {
        view.showToast( R.string.COMMENT_saved);
        view.hideCommentInProgress();
        view.resetCommentForm();
        view.appendComment(comment);
        cardsService.updateCommentsCounter(comment.getCardId(), 1);
    }

    @Override
    public void onCommentSaveError(String errorMsg) {
        view.hideCommentInProgress();
        view.enableCommentForm();
        view.showErrorMsg(errorMsg);
    }

    @Override
    public void onCommentsLoadSuccess(List<Comment> list) {
        view.hideCommentsThrobber();
        view.displayComments(list);
    }

    @Override
    public void onCommentsLoadError(String errorMessage) {
        view.showErrorMsg(R.string.CARD_SHOW_error_loading_comments);
    }

    @Override
    public void onDeleteSuccess(Comment comment) {
        cardsService.updateCommentsCounter(currentCard.getKey(), -1);
        // TODO: а можно сделать список "живым"...
        view.removeComment(comment);
    }

    @Override
    public void onDeleteError(String msg) {
        view.showErrorMsg(R.string.COMMENT_delete_error, msg);
    }


    // Внутренние методы
    private void postComment(Comment comment) {
        commentsService.createComment(comment, this);
    }
}