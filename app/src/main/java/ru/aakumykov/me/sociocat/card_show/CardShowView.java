package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.ListAdapterCommentsCard;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter;
import ru.aakumykov.me.sociocat.card_show.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.presenters.CardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.CommentsPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCommentsPresenter;
import ru.aakumykov.me.sociocat.models.Comment;


public class CardShowView extends BaseView implements
        iListView,
        iCommentFormView
{
    public interface LoadCommentsCallbacks {
        void onLoadCommentsSuccess(List<Comment> list);
        void onLoadCommentsFail(String errorMsg);
    }

    private final static String TAG ="CardShowView";

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private iCardPresenter cardPresenter;
    private iCommentsPresenter commentsPresenter;
    private iListAdapter listAdapter;
    private boolean firstRun = true;
    private ru.aakumykov.me.sociocat.card_show.comment_form.iCommentForm commentForm;


    // Системные методы
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        this.commentsPresenter = new CommentsPresenter();
        this.cardPresenter = new CardPresenter(commentsPresenter);

        this.listAdapter = new ListAdapterCommentsCard();

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter((RecyclerView.Adapter) listAdapter);

        this.commentForm = new CommentForm(this);
        this.commentForm.attachTo(commentFormContainer);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);
    }

    @Override protected void onStart() {
        super.onStart();

        cardPresenter.bindViewAdapter((iListAdapter_Card) listAdapter);
        cardPresenter.bindReplyView(this);

        commentsPresenter.bindViewAdapter((iListAdapter_Comments) listAdapter);
        commentsPresenter.bindReplyView(this);

        listAdapter.bindPresenters(cardPresenter, commentsPresenter);
        listAdapter.bindListView(this);

        if (firstRun) {
            firstRun = false;
            processInputIntent();
            // TODO: чья ответственность обрабатывать Intent?
            // TODO: это ответственность Activity Фсешмшен.
        }
    }

    @Override protected void onStop() {
        super.onStop();

        cardPresenter.unbindViewAdapter();
        cardPresenter.unbindReplyView();

        commentsPresenter.unbindViewAdapter();
        commentsPresenter.unbindReplyView();

        listAdapter.unbindPresenters();
        listAdapter.unbindListView();
    }

    @Override public void onBackPressed() {
        if (commentForm.isVisible())
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


    // iListView
    @Override public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
//        recyclerView.scrollToPosition(-1);
    }


    // iCommentFormView
    @Override public void showCommentForm(@Nullable String quotedText, ListItem parentItem) {
        if (null != quotedText)
            commentForm.setQuote(quotedText);

        commentForm.addSendButtonListener(new ru.aakumykov.me.sociocat.card_show.comment_form.iCommentForm.SendButtonListener() {
            @Override
            public void onSendCommentClicked(String commentText) {
                commentsPresenter.sendCommentClicked(commentText, parentItem, commentForm);
            }
        });

        commentForm.show();
    }

    @Override public void hideCommentForm() {
        commentForm.hide();
    }


    // Внутренние методы
    private void showRepliedText(String text) {
//        if (!TextUtils.isEmpty(text)) {
//            repliedCommentTextView.setText(text);
//            MyUtils.show(repliedCommentContainer);
//        }
    }

    private void hideRepliedText() {
//        repliedCommentTextView.setText("");
//        MyUtils.hide(repliedCommentContainer);
    }


    private void processInputIntent() {
        Intent intent = getIntent();

        try {
            String cardKey = intent.getStringExtra(Constants.CARD_KEY);
            String commentKey =intent.getStringExtra(Constants.COMMENT_KEY);

            cardPresenter.onWorkBegins(cardKey, commentKey);
        }
        catch (Exception e) {
            cardPresenter.onErrorOccurs();

            showErrorMsg(R.string.CARD_SHOW_error_loading_card, e);
            e.printStackTrace();
        }
    }

    private void scrollToComment(Comment comment) {
//        int position = dataAdapter.findCommentPosition(comment);
//        if (-1 != position)
//            recyclerView.scrollToPosition(position);
//        else
//            MyUtils.showCustomToast(this, "Комментарий не найден");
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
