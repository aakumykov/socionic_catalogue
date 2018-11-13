package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

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
        iCommentsSingleton.ListCallbacks
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
    public void loadComments(String cardId) {
        commentsService.loadList(cardId, this);
    }

    // Добавление комментария
    @Override
    public void postComment(String text) {
        view.disableCommentForm();
        Comment comment = new Comment(text, currentCard.getKey(), null, authService.currentUid());
        commentsService.createComment(comment, this);
    }


    // Реакция на кнопки
    @Override
    public void onTagClicked(String tagName) {
        view.goList(tagName);
    }

    @Override
    public void onEditButtonClicked() {
        view.goEditPage(currentCard);
    }

    @Override
    public void onDeleteButtonClicked() {
        Log.d(TAG, "onDeleteButtonClicked()");
//        if (authService.isAuthorized()) view.showDeleteDialog();
//        else view.showErrorMsg(R.string.not_authorized);
        view.showDeleteDialog();
    }

    @Override
    public void onDeleteConfirmed() {
        view.showProgressBar();
        view.showInfoMsg(R.string.deleting_card);

        try {
            cardsService.deleteCard(currentCard, this);
        } catch (Exception e) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.error_deleting_card);
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

        loadComments(card.getKey());
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
    }

    @Override
    public void onCommentCreateError(String errorMsg) {
        view.enableCommentForm();
        view.showErrorMsg(errorMsg);
    }

    @Override
    public void onCommentsLoadSuccess(List<Comment> list) {
        view.displayComments(list);
    }

    @Override
    public void onCommentsLoadError(String errorMessage) {
        view.showErrorMsg(R.string.CARD_SHOW_error_loading_comments);
    }
}
