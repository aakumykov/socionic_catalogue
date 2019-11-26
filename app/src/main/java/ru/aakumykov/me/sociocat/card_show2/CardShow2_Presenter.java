package ru.aakumykov.me.sociocat.card_show2;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CardShow2_Presenter implements iCardShow2.iPresenter {

    private final static String TAG = "CardShow2_Presenter";
    private AuthSingleton authSingleton = AuthSingleton.getInstance();
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();
    private CardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private CommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iCardShow2.iPageView pageView = null;
    private iCardShow2.iDataAdapter dataAdapter = null;
    private Card currentCard = null;
    private iList_Item commentedItem = null;


    // iCardShow2.iPresenter
    @Override
    public void bindView(iCardShow2.iPageView view) {
        this.pageView = view;
    }

    @Override
    public void unbindView() {
        this.pageView = new CardShow2_ViewStub();
    }

    @Override
    public void bindDataAdapter(iCardShow2.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unbindDataAdapter() {
        this.dataAdapter = null;
    }

    @Override
    public void onPageOpened(String cardKey) {

        dataAdapter.showCardThrobber();

        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;
                pageView.setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
                dataAdapter.showCard(card);
                loadComments(card.getKey(), null, null);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, msg);
            }
        });
    }

    @Override
    public void onLoadMoreClicked(int position) {
        Comment previousComment = dataAdapter.getComment(position-1);
        Comment nextComment = dataAdapter.getComment(position+1);
        loadComments(currentCard.getKey(), previousComment, nextComment, position);
    }

    @Override
    public void onAddCommentClicked(iList_Item listItem) {
        this.pageView.showCommentForm();
        this.commentedItem = listItem;
    }

    @Override
    public void onSendCommentClicked() {
        String commentText = pageView.getCommentText().trim();
        if (TextUtils.isEmpty(commentText))
            return;

        Comment comment = new Comment(pageView.getCommentText());
                comment.setCardId(currentCard.getKey());
                comment.setCommentText(pageView.getCommentText());
                comment.setParent((iCommentable) this.commentedItem.getPayload());
                comment.setUser(usersSingleton.getCurrentUser());
                comment.setCreatedAt(new Date().getTime());

        pageView.disableCommentForm();

        commentsSingleton.createComment(comment, new iCommentsSingleton.CreateCallbacks() {
            @Override
            public void onCommentSaveSuccess(Comment comment) {
                dataAdapter.appendOneComment(comment);
                pageView.clearCommentForm();
                pageView.hideCommentForm();
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                pageView.showCommentError(R.string.COMMENT_error_adding_comment, errorMsg);
            }
        });

    }


    // Внутренние методы
    private void loadComments(String cardKey, @Nullable Comment startAfterComment, @Nullable Comment endBoundaryComment) {
        loadComments(cardKey, startAfterComment, endBoundaryComment, null);
    }

    private void loadComments(String cardKey, @Nullable Comment startAfterComment, @Nullable Comment endBoundaryComment, @Nullable Integer insertPosition) {

        dataAdapter.showCommentsThrobber(insertPosition);

        commentsSingleton.loadList(cardKey, startAfterComment, endBoundaryComment, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                if (null != insertPosition)
                    dataAdapter.insertComments(list, insertPosition);
                else
                    dataAdapter.appendComments(list);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                pageView.showErrorMsg(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }
}
