package ru.aakumykov.me.sociocat.card_show;

import android.app.Activity;
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
import ru.aakumykov.me.sociocat.card_show.adapter.ListAdapter;
import ru.aakumykov.me.sociocat.card_show.adapter.iCardView;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter;
import ru.aakumykov.me.sociocat.utils.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.presenters.CardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.CommentsPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCommentsPresenter;
import ru.aakumykov.me.sociocat.models.Comment;


public class CardShow_View extends BaseView implements
        iPageView,
        iCardShow_View
{
    public interface LoadCommentsCallbacks {
        void onLoadCommentsSuccess(List<Comment> list);
        void onLoadCommentsFail(String errorMsg);
    }

    private final static String TAG ="CardShow_View";

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private iCardPresenter cardPresenter;
    private iCommentsPresenter commentsPresenter;
    private iListAdapter listAdapter;
    private iCommentForm commentForm;
    private boolean firstRun = true;


    // Системные методы
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        this.commentsPresenter = new CommentsPresenter();
        this.cardPresenter = new CardPresenter(commentsPresenter);

        this.listAdapter = new ListAdapter();

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter((RecyclerView.Adapter) listAdapter);

        this.commentForm = new CommentForm(this);
        this.commentForm.attachTo(commentFormContainer);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bindComponents();

        switch (requestCode) {
            case Constants.CODE_REPLY_TO_CARD:
                processReplyToCard(resultCode);
                break;

            case Constants.CODE_REPLY_TO_COMMENT:
                processReplyToComment(resultCode, data);
                break;

            default:
                showErrorMsg(R.string.CARD_SHOW_data_error, "onActivityResult(), unknown request code: "+requestCode);
                break;
        }
    }

    @Override protected void onStart() {
        super.onStart();

        bindComponents();

        if (firstRun) {
            firstRun = false;
            processInputIntent();
            // TODO: чья ответственность обрабатывать Intent?
            // TODO: это ответственность Activity Фсешмшен.
        }
    }

    @Override protected void onStop() {
        super.onStop();
        unbindComponents();
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


    // iPageView
    @Override
    public Activity getActivity() {
        return (Activity) this;
    }

    @Override
    public void showCommentForm(ListItem repliedItem) {
        String repliedText = repliedItem.isCommentItem() ? ((Comment)repliedItem).getText() : null;
        commentForm.setQuote(repliedText);
        commentForm.show();
    }


    // iCommentFormView
    @Override public void showCommentForm(@Nullable String quotedText, ListItem parentItem) {
        if (null != quotedText)
            commentForm.setQuote(quotedText);

        commentForm.addSendButtonListener(new ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm.SendButtonListener() {
            @Override
            public void onSendCommentClicked(String commentText) {
                commentsPresenter.onSendCommentClicked(commentText, parentItem, commentForm);
            }
        });

        commentForm.show();
    }

    @Override public void hideCommentForm() {
        commentForm.hide();
    }

    @Override public void scrollListToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }


    // Внутренние методы
    private void bindComponents() {
        cardPresenter.bindPageView(this);
        cardPresenter.bindListAdapter((iCardView) listAdapter);

        commentsPresenter.bindPageView(this);
        commentsPresenter.bindCommentsView((iCommentsView) listAdapter);

        listAdapter.bindPresenters(cardPresenter, commentsPresenter);
        listAdapter.bindView(this);
    }

    private void unbindComponents() {
        cardPresenter.unbindPageView();
        cardPresenter.unbindListAdapter();

        commentsPresenter.unbindPageView();
        commentsPresenter.unbindCommentsView();

        listAdapter.unbindPresenters();
        listAdapter.unbindView();
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
            showErrorMsg(R.string.CARD_SHOW_error_loading_card, e.getMessage());
            e.printStackTrace();
        }
    }

    private void processReplyToCard(int resultCode) {
        if (RESULT_OK != resultCode) {
            showToast(R.string.CARD_SHOW_login_required_to_comment);
            return;
        }

        cardPresenter.onReplyClicked();
    }

    private void processReplyToComment(int resultCode, @Nullable Intent data) {
        if (RESULT_OK != resultCode) {
            showToast(R.string.CARD_SHOW_login_required_to_comment);
            return;
        }

        if (null == data) {
            showErrorMsg(R.string.CARD_SHOW_data_error, "Intent from onActivityResult() is NULL");
            return;
        }

        Bundle transitArguments = data.getBundleExtra(Constants.TRANSIT_ARGUMENTS);
        if (null != transitArguments) {
            String commentKey = transitArguments.getString(Constants.COMMENT_KEY);
            commentsPresenter.onReplyToCommentClicked(commentKey);
        }
    }
}
