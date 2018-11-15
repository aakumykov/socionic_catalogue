package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.file.AccessDeniedException;
import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.CommentsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.utils.MyUtils;

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


    // Добавление комментария
    @Override
    public void postComment(String text) {
//        view.disableCommentForm();
        Comment comment = new Comment(text, currentCard.getKey(), null, authService.currentUid());
        commentsService.createComment(comment, this);
    }

    private void postComment(Comment comment) {
        commentsService.createComment(comment, this);
    }

    @Override
    public void replyToComment(String commentId) {
//        Comment comment
    }


    // Удаление комментария
    @Override
    public void deleteComment(Comment comment) {
        if (authService.isUserLoggedIn()) {
            if (authService.currentUid().equals(comment.getUserId())) {
                view.showCommentDeleteDialog(comment);
            }
        }
    }

    @Override
    public void onCommentDeleteConfirmed(Comment comment) throws Exception {
        // TODO: переделать проверку по-правильному
        if (authService.currentUid().equals(comment.getUserId())) {
            commentsService.deleteComment(comment, this);
        } else {
            throw new IllegalAccessException("Only author can delete ceoment.");
        }
    }


    // Изменение комментария
    @Override
    public void editComment(Comment comment) {
        if (authService.isUserLoggedIn()) {
            if (authService.currentUid().equals(comment.getUserId())) {
                view.showCommentEditDialog(comment);
            }
        }
    }

    @Override
    public void onEditCommentConfirmed(Comment comment) throws Exception {

    }

    // Реакция на кнопки
    @Override
    public void onTagClicked(String tagName) {
        view.goList(tagName);
    }

    @Override
    public void editCard() {
        view.goEditPage(currentCard);
    }

    @Override
    public void deleteCard(Card card) {
        if (authService.isUserLoggedIn()) {
            // TODO: или Админ
            if (authService.currentUid().equals(card.getUserId())) {
                view.showCardDeleteDialog();
            }
        }
    }

    @Override
    public void onCardDeleteConfirmed(Card card) throws Exception {
        view.showProgressBar();
        view.showInfoMsg(R.string.deleting_card);

        try {
            cardsService.deleteCard(currentCard, this);
        } catch (Exception e) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.error_deleting_card);
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
        TagsSingleton.getInstance().updateCardTags(card.getKey(), card.getTags(), null, null);
        view.closePage();
    }

    @Override
    public void onCardDeleteError(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.error_deleting_card);
    }


    @Override
    public void onCommentCreateSuccess(Comment comment) {
        view.showInfoMsg("Комментарий добавлен");
        view.appendComment(comment);
        view.resetCommentForm();

        cardsService.updateCommentsCounter(comment.getCardId(), 1);
    }

    @Override
    public void onCommentCreateError(String errorMsg) {
//        view.enableCommentForm();
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
}
