package ru.aakumykov.me.sociocat.card_show2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.services.Comments_Service;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class CardShow2_View extends BaseView implements
        iCardShow2_View,
        iCardController,
        iCommentsController
{
    public interface LoadCommentsCallbacks {
        void onLoadCommentsSuccess(List<Comment> list);
        void onLoadCommentsFail(String errorMsg);
    }

    private final static String TAG ="CardShow2_View";

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.item_list) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) View commentFormContainer;
    @BindView(R.id.parentCommentContainer) View parentCommentContainer;
    @BindView(R.id.parentCommentTextView) TextView parentCommentTextView;
    @BindView(R.id.parentCommentDiscardWidget) View parentCommentDiscardWidget;
    @BindView(R.id.commentInput) EditText commentInput;
    @BindView(R.id.sendCommentWidget) View sendCommentWidget;

    private DataAdapter dataAdapter;
//    private boolean flagCommentsLoadInProgress = false;

    private iCardController cardController;
    private iCommentsController commentsController;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show2_activity);
        ButterKnife.bind(this);

        cardController = this;
        commentsController = this;

        dataAdapter = new DataAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dataAdapter);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);

        processInputIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataAdapter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataAdapter.unbindView();
    }

    @Override
    public void onBackPressed() {
        if (View.VISIBLE == commentFormContainer.getVisibility())
            hideCommentForm();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    // Нажатия
    @OnClick(R.id.parentCommentDiscardWidget)
    void hideParentCommentPiece() {
        parentCommentTextView.setText("");
        parentCommentContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.sendCommentWidget)
    void postComment() {

/*
        Comment comment = new Comment(commentInput.getText().toString());
        disableCommentForm();

        commentsService.postComment(comment, new Comments_Service.iPostCommentCallbacks() {

            @Override
            public void onPostCommentSuccess(Comment comment) {

                hideCommentForm();
                dataAdapter.appendComment(comment, new iDataAdapter.AppendCommentCallbacks() {

                    @Override
                    public void onCommentAppended() {
                        scrollToComment(comment);
                    }

                });
            }

            @Override
            public void onPostCommentFail(String errorMsg) {
                showError(errorMsg);
                enableCommentForm();
            }
        });
*/

    }


    // Методы iBaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Методы iCardShow2_View
    @Override public void displayCard(Card card) {
        setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
        dataAdapter.setCard(card);
    }

    @Override public void displayComments(List<Comment> list) {
        dataAdapter.hideLastServiceItem();
         dataAdapter.appendComments(list);
        dataAdapter.showLoadMoreItem();
    }

    @Override public void showCardThrobber() {
        showProgressMessage(R.string.CARD_SHOW_loading_card);
    }

    @Override public void hideCardThrobber() {
        hideProgressMessage();
    }

    @Override public void showCommentsThrobber() {
        dataAdapter.showCommentsThrobber();
    }

    @Override public void hideCommentsThrobber() {
        dataAdapter.hideCommentsThrobber();
    }


    // Методы iCardController
    @Override
    public void loadCard(String cardKey, @Nullable String commentKey) {

        showProgressMessage(R.string.CARD_SHOW_loading_card);

        CardsSingleton.getInstance().loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                hideProgressMessage();

                displayCard(card);

                commentsController.loadComments(card.getKey(), null, 10);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                showErrorMsg(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });

    }


    // Методы iCommentsController
    @Override public void loadComments(String parentCardId, @Nullable String start, int count) {
        dataAdapter.showCommentsThrobber();

        CommentsSingleton.getInstance().loadList(parentCardId, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                dataAdapter.hideCommentsThrobber();
                displayComments(list);
            }

            @Override
            public void onCommentsLoadError(String errorMsg) {
                dataAdapter.hideCommentsThrobber();
                showErrorMsg(R.string.CARD_SHOW_error_loading_comments, errorMsg);
            }
        });
    }

    @Override public void editComment(Comment comment) {

    }

    @Override public void showCommentForm(Item parentItem) {
        enableCommentForm();
        commentFormContainer.setVisibility(View.VISIBLE);

        if (parentItem instanceof Comment) {
            String parentCommentText = MyUtils.cutToLength(((Comment) parentItem).getText(), 20);
            parentCommentTextView.setText(parentCommentText);
            parentCommentContainer.setVisibility(View.VISIBLE);
        }
        else {
            hideParentCommentPiece();
        }
    }

    @Override public void hideCommentForm() {
        hideParentCommentPiece();
        commentInput.setText("");
        commentFormContainer.setVisibility(View.GONE);
    }

    @Override public void enableCommentForm() {
        commentInput.setEnabled(true);
        sendCommentWidget.setEnabled(true);
    }

    @Override public void disableCommentForm() {
        commentInput.setEnabled(false);
        sendCommentWidget.setEnabled(false);
    }


    // Внутренние методы
    private void processInputIntent() {
        Intent intent = getIntent();

        if (null != intent) {
            String cardKey = intent.getStringExtra(Constants.CARD_KEY);
            String commentKey =intent.getStringExtra(Constants.COMMENT_KEY);
            cardController.loadCard(cardKey, commentKey);
        }
        else {
            showErrorMsg(R.string.CARD_SHOW_error_loading_card, "Intent is NULL");
        }
    }

    private void scrollToComment(Comment comment) {
        int position = dataAdapter.findCommentPosition(comment);
        if (-1 != position)
            recyclerView.scrollToPosition(position);
        else
            MyUtils.showCustomToast(this, "Комментарий не найден");
    }


//    private void refreshCard() {
//        dataAdapter.clearList();
//        processInputIntent();
//    }

//    private void setupAutoCommentsLoading() {
//        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (!recyclerView.canScrollVertically(1)) {
//                    Comment lastComment = dataAdapter.getLastComment();
//                    if (null != lastComment)
//                        loadComments(lastComment.getKey(), 10);
//                    else
//                        // Что тогда?
//                }
//            }
//        });*/
//    }

//    @Override
//    public void loadComments(String start, int count) {
//        if (!flagCommentsLoadInProgress) {
//
//            flagCommentsLoadInProgress = true;
//            dataAdapter.hideLoadMoreItem();
//            dataAdapter.showCommentsThrobber();
//
//            new Comments_Service().loadComments(start, count, new Comments_Service.iLoadCommentsCallbacks() {
//                @Override
//                public void onCommentsLoadSuccess(List<Comment> list) {
//                    dataAdapter.hideCommentsThrobber();
//                    dataAdapter.appendComments(list);
//                    flagCommentsLoadInProgress = false;
//                }
//
//                @Override
//                public void onCommentsLoadFail(String errorMsg) {
//                    showErrorMsg(R.string.CARD_SHOW_error_loading_comments, errorMsg);
//                    flagCommentsLoadInProgress = false;
//                }
//            });
//        }
//
//    }
}
