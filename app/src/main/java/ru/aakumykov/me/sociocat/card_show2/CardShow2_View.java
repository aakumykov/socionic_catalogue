package ru.aakumykov.me.sociocat.card_show2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.controllers.CardController;
import ru.aakumykov.me.sociocat.card_show2.controllers.CommentsController;
import ru.aakumykov.me.sociocat.card_show2.controllers.iCardController;
import ru.aakumykov.me.sociocat.card_show2.controllers.iCommentsController;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class CardShow2_View extends BaseView implements
        iCardShow2_View
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
    @BindView(R.id.parentCommentContainer) View repliedCommentContainer;
    @BindView(R.id.parentCommentTextView) TextView repliedCommentTextView;
    @BindView(R.id.repliedCommentDiscardWidget) View parentCommentDiscardWidget;
    @BindView(R.id.commentInput) EditText commentInput;
    @BindView(R.id.sendCommentWidget) View sendCommentWidget;

    private iCardController cardController;
    private iCommentsController commentsController;
    private DataAdapter dataAdapter;
    private boolean firstRun = true;
    //    private boolean flagCommentsLoadInProgress = false;


    // Системные методы
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show2_activity);
        ButterKnife.bind(this);

        this.cardController = new CardController();
        this.commentsController = new CommentsController();

        dataAdapter = new DataAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dataAdapter);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);
    }

    @Override protected void onStart() {
        super.onStart();

        cardController.bindView(this);
        commentsController.bindView(this);
        dataAdapter.bindControllers(cardController, commentsController);

        if (firstRun) {
            firstRun = false;
            processInputIntent(); // TODO: чья ответственность обрабатывать Intent?
        }
    }

    @Override protected void onStop() {
        super.onStop();
        cardController.unbindView();
        commentsController.unbindView();
        dataAdapter.unbindControllers();
    }

    @Override public void onBackPressed() {
        if (View.VISIBLE == commentFormContainer.getVisibility())
            hideCommentForm();
        else
            super.onBackPressed();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override public void onUserLogin() {

    }

    @Override public void onUserLogout() {

    }


    // Интерфейсные методы
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


    @Override public void showCommentForm(Item repliedItem) {
        String repliedText = "";
        if (repliedItem instanceof Comment) {
            Comment comment = (Comment) repliedItem;
            repliedText = MyUtils.cutToLength(comment.getText(), 20);
        }

        showRepliedText(repliedText);
        MyUtils.show(commentFormContainer);
    }

    @Override public void hideCommentForm() {
        hideRepliedText();
        MyUtils.hide(commentFormContainer);
    }

    @Override public void enableCommentForm() {
        MyUtils.enable(commentInput);
        MyUtils.enable(sendCommentWidget);
    }

    @Override public void disableCommentForm() {
        MyUtils.disable(commentInput);
        MyUtils.disable(sendCommentWidget);
    }



    @OnClick(R.id.repliedCommentDiscardWidget)
    void onRemoveRepliedText() {
        hideRepliedText();
    }


    @OnClick(R.id.sendCommentWidget)
    void postComment() {

        MyUtils.showCustomToast(this, "Ещё не реализовано");

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


    // Внутренние методы
    private void showRepliedText(String text) {
        if (!TextUtils.isEmpty(text)) {
            repliedCommentTextView.setText(text);
            MyUtils.show(repliedCommentContainer);
        }
    }

    private void hideRepliedText() {
        repliedCommentTextView.setText("");
        MyUtils.hide(repliedCommentContainer);
    }

    private void processInputIntent() {
        Intent intent = getIntent();

        if (null != intent) {
            String cardKey = intent.getStringExtra(Constants.CARD_KEY);
            String commentKey =intent.getStringExtra(Constants.COMMENT_KEY);

            cardController.loadCard(cardKey, commentKey, new iCardController.LoadCardCallbacks() {
                @Override public void onCardLoaded(Card card) {
                    commentsController.loadComments(card.getKey(), null, Config.DEFAULT_COMMENTS_LOAD_COUNT);
                }
            });
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
