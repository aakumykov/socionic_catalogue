package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iStorageSingleton;
import ru.aakumykov.me.sociocat.interfaces.iTagsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;


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
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private Card currentCard;


    // Получение карточки
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {

        if (null == intent) throw new IllegalArgumentException("intent == null");

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey) throw new Exception("Intent has no CARD_KEY");

        if (null != view) {
            view.showProgressBar();
        }

        cardsSingleton.loadCard(cardKey, this);
    }

    @Override
    public void loadComments(Card card) {
        if (card.getCommentsCount() > 10) view.showCommentsThrobber();
        commentsSingleton.loadList(card.getKey(), this);
    }


    // Изменение рейтинга
    @Override
    public void rateCardUp() {
        if (authSingleton.isUserLoggedIn()) {

            if (null != view) {
                view.showCardRatingThrobber();
            }

            cardsSingleton.rateUp(currentCard.getKey(), authSingleton.currentUserId(), new iCardsSingleton.RatingCallbacks() {
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
                    if (null != view) {
                        view.onCardRateError();
                    }
                    Log.e(TAG, errorMsg);
                }
            });
        }
    }

    @Override
    public void rateCardDown() {
        if (authSingleton.isUserLoggedIn()) {

            if (null != view) {
                view.showCardRatingThrobber();
            }

            cardsSingleton.rateDown(currentCard.getKey(), authSingleton.currentUserId(), new iCardsSingleton.RatingCallbacks() {
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
                    if (null != view) {
                        view.onCardRateError();
                    }
                    Log.e(TAG, errorMsg);
                }
            });
        }
    }


    // Добавление комментария
    @Override
    public void postComment(String text) {

        if (!authSingleton.isUserLoggedIn()) {
            if (null != view) {
                view.showToast(R.string.INFO_you_must_be_logged_in);
            }
            return;
        }

        Comment comment = new Comment(
                text,
                currentCard.getKey(),
                null,
                null,
                authSingleton.currentUserId(),
                authSingleton.currentUserName(),
                authSingleton.currentUser().getAvatarURL()
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
                authSingleton.currentUserId(),
                authSingleton.currentUserName(),
                authSingleton.currentUser().getAvatarURL()
        );
        postComment(comment);
    }


    // Удаление комментария
    @Override
    public void deleteCommentConfirmed(Comment comment) {
        // TODO: переделать проверку по-правильному
        if (!authSingleton.isUserLoggedIn())
            return;

        // TODO: эта проверка без проверки на залогиненность...
        if (!authSingleton.currentUserId().equals(comment.getUserId())) {
            if (null != view) {
                view.showErrorMsg(R.string.action_denied);
            }
            return;
        }

        try {
            commentsSingleton.deleteComment(comment, this);
        } catch (Exception e) {
            if (null != view) {
                view.showErrorMsg(R.string.COMMENT_delete_error);
            }
            e.printStackTrace();
        }
    }


    // Изменение комментария
    @Override
    public void editCommentConfirmed(final Comment comment) {
        // TODO: контроль длины

        if (!authSingleton.isUserLoggedIn())
            return;

        if (!authSingleton.currentUserId().equals(comment.getUserId())) {
            if (null != view) {
                view.showErrorMsg(R.string.action_denied);
            }
        }

        if (!TextUtils.isEmpty(comment.getText())) {
            try {
                if (null != view) {
                    view.showCommentInProgress();
                }
                commentsSingleton.updateComment(comment, new iCommentsSingleton.CreateCallbacks() {
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
                if (null != view) {
                    view.showErrorMsg(R.string.COMMENT_save_error);
                }
                e.printStackTrace();
            }
        }
    }


    // Оценка комментария
    @Override
    public void rateCommentUp(Comment comment, iCommentsSingleton.RatingCallbacks callbacks) {
        if (authSingleton.isUserLoggedIn()) {
            commentsSingleton.rateUp(comment.getKey(), authSingleton.currentUserId(), callbacks);
        }
    }

    @Override
    public void rateCommentDown(Comment comment, iCommentsSingleton.RatingCallbacks callbacks) {
        if (authSingleton.isUserLoggedIn()) {
            commentsSingleton.rateDown(comment.getKey(), authSingleton.currentUserId(), callbacks);
        }
    }


    // Реакция на кнопки
    @Override
    public void onTagClicked(String tagName) {
        view.goList(tagName);
    }

    @Override
    public void cardDeleteConfirmed(Card card) {
        if (null != view) {
            view.showProgressBar();
            view.showInfoMsg(R.string.deleting_card);
        }

        if (!authSingleton.isUserLoggedIn()) return;

        // TODO: "или Админ"
        if (!authSingleton.currentUserId().equals(card.getUserId())) {
            if (null != view) {
                view.showErrorMsg(R.string.action_denied);
            }
            return;
        }

        try {
            cardsSingleton.deleteCard(currentCard, this);
        } catch (Exception e) {
            if (null != view) {
                view.hideProgressBar();
                view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card);
            }
            e.printStackTrace();
        }
    }


    // Link / Unlink
    @Override
    public void linkView(iCardShow.View view) {
        if (null != view) {
            this.view = view;
        }
    }
    @Override
    public void unlinkView() {
        if (null != view) {
            this.view = null;
        }
    }


    // Коллбеки
    @Override
    public void onCardLoadSuccess(Card card) {
        if (null != card) {
            this.currentCard = card;
            if (null != view)
                view.displayCard(card);
            loadComments(card);

        } else {
            if (null != view)
                view.showErrorMsg(R.string.CARD_SHOW_error_card_not_exists);
        }
    }

    @Override
    public void onCardLoadFailed(String msg) {
        this.currentCard = null;
        if (null != view) {
            view.showErrorMsg(R.string.card_load_error);
        }
    }

    @Override
    public void onCardDeleteSuccess(Card card) {
        try {
            TagsSingleton.getInstance().updateCardTags(
                    card.getKey(),
                    card.getTags(),
                    null,
                    new iTagsSingleton.UpdateCallbacks() {
                        @Override
                        public void onUpdateSuccess() {

                        }

                        @Override
                        public void onUpdateFail(String errorMsg) {
                            if (null != view)
                                view.showErrorMsg(R.string.CARD_SHOW_error_deleting_tags, errorMsg);
                        }
                    }
            );

            if (card.isImageCard()) {
                String imageFileName = card.getFileName();
                StorageSingleton.getInstance().deleteImage(imageFileName, new iStorageSingleton.FileDeletionCallbacks() {
                    @Override
                    public void onDeleteSuccess() {

                    }

                    @Override
                    public void onDeleteFail(String errorMsg) {
                        if (null != view)
                            view.showErrorMsg(R.string.CARD_SHOW_error_deleting_image, errorMsg);
                    }
                });
            }

            commentsSingleton.deleteCommentsForCard(currentCard.getKey());

        } catch (Exception e) {
            if (null != view)
                view.showErrorMsg(e.getMessage());
            e.printStackTrace();
        }

        if (null != view)
            view.finishAfterCardDeleting(card);
    }

    @Override
    public void onCardDeleteError(String msg) {
        if (null != view) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card);
        }
    }


    @Override
    public void onCommentSaveSuccess(Comment comment) {
        if (null != view) {
            view.showToast(R.string.COMMENT_saved);
            view.hideCommentInProgress();
            view.resetCommentForm();
            view.appendComment(comment);
        }
        cardsSingleton.updateCommentsCounter(comment.getCardId(), 1);
    }

    @Override
    public void onCommentSaveError(String errorMsg) {
        if (null != view) {
            view.hideCommentInProgress();
            view.enableCommentForm();
            view.showErrorMsg(errorMsg);
        }
    }

    @Override
    public void onCommentsLoadSuccess(List<Comment> list) {
        if (null != view) {
            view.hideCommentsThrobber();
            view.displayComments(list);
        }
    }

    @Override
    public void onCommentsLoadError(String errorMessage) {
        if (null != view) {
            view.showErrorMsg(R.string.CARD_SHOW_error_loading_comments);
        }
    }

    @Override
    public void onDeleteSuccess(Comment comment) {
        cardsSingleton.updateCommentsCounter(currentCard.getKey(), -1);
        // TODO: а можно сделать список "живым"...
        if (null != view) {
            view.removeComment(comment);
        }
    }

    @Override
    public void onDeleteError(String msg) {
        if (null != view) {
            view.showErrorMsg(R.string.COMMENT_delete_error, msg);
        }
    }


    // Внутренние методы
    private void postComment(Comment comment) {
        commentsSingleton.createComment(comment, this);
    }
}
