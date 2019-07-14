package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.adapter.DataAdapter;
import ru.aakumykov.me.sociocat.card_show.adapter.iCardView;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.adapter.iDataAdapter;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;
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
    @BindView(R.id.progressBar1) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private iCardPresenter cardPresenter;
    private iCommentsPresenter commentsPresenter;
    private iDataAdapter listAdapter;
    private iCommentForm commentForm;
    private boolean firstRun = true;
    private boolean mEditMode;


    // Системные методы
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        this.commentsPresenter = new CommentsPresenter();
        this.cardPresenter = new CardPresenter(commentsPresenter);

        this.listAdapter = new DataAdapter();

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
            case Constants.CODE_POST_REPLY:
                processReply(resultCode, data);
                break;
            case Constants.CODE_LOGIN:
                break;
            case Constants.CODE_EDIT_CARD:
                processCardEditionResult(resultCode, data);
                break;
            default:
                showErrorMsg(R.string.CARD_SHOW_data_error, "Unknown request code: "+requestCode);
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
            hideCommentForm(true);
        else
            super.onBackPressed();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        if (cardPresenter.canEditCard())
            menuInflater.inflate(R.menu.edit, menu);

        if (cardPresenter.canDeleteCard())
            menuInflater.inflate(R.menu.delete, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionEdit:
                cardPresenter.onEditClicked();
                break;
            case R.id.actionDelete:
                cardPresenter.onDeleteClicked();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override public void onUserLogin() {

    }

    @Override public void onUserLogout() {

    }


    @Override
    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void goEditCard(Card card) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    @Override
    public void goShowCardsWithTag(String tagName) {
//        Intent intent = new Intent()
    }


    // iPageView
    @Override
    public void showCommentForm(iTextItem item, boolean editMode) {

        String text = item.isCommentItem() ? ((Comment)item).getText() : null;

        if (null != text) {
            if (editMode)
                commentForm.setText(text);
            else
                commentForm.setQuote(text);
        }

        mEditMode = editMode;

        commentForm.addButtonListeners(new iCommentForm.ButtonListeners() {
            @Override public void onClearQuoteClicked() {
                Card card = cardPresenter.getCard();
                commentsPresenter.onReplyClicked(card);
            }

            @Override
            public void onSendCommentClicked(String commentText) {
                commentsPresenter.onSendCommentClicked(commentForm);
            }
        });

        commentForm.show(editMode);
    }

    @Override
    public void hideCommentForm(boolean withQuestion) {

        if (commentForm.isEmpty()) {
            commentForm.hide();
            return;
        }

        if (withQuestion && mEditMode) {
            MyDialogs.cancelEditDialog(
                    this,
                    R.string.CARD_SHOW_cancel_comment_edition_title,
                    R.string.CARD_SHOW_cancel_comment_edittion_message,
                    new iMyDialogs.StandardCallbacks() {
                        @Override
                        public void onCancelInDialog() {

                        }

                        @Override
                        public void onNoInDialog() {

                        }

                        @Override
                        public boolean onCheckInDialog() {
                            return true;
                            // TODO: проверить с false
                        }

                        @Override
                        public void onYesInDialog() {
                            commentForm.clear();
                            commentForm.hide();
                        }
                    }
            );
        }
        else {
            commentForm.hide();
        }
    }


    @Override
    public void scrollListToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }


    // Внутренние методы
    private void bindComponents() {
        cardPresenter.bindPageView(this);
        cardPresenter.bindListAdapter((iCardView) listAdapter);

        commentsPresenter.bindPageView(this);
        commentsPresenter.bindCommentsView((iCommentsView) listAdapter);

        listAdapter.bindPresenters(cardPresenter, commentsPresenter);
        listAdapter.bindView(this, this);
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

    private void processReply(int resultCode, @Nullable Intent data) {

        if (RESULT_OK != resultCode) {
            showToast(R.string.CARD_SHOW_login_required_to_comment);
            return;
        }

        if (null == data) {
            showErrorMsg(R.string.CARD_SHOW_data_error, "Intent from onActivityResult() is NULL");
            return;
        }

        Bundle transitArguments = data.getBundleExtra(Constants.TRANSIT_ARGUMENTS);
        if (null == transitArguments) {
            showErrorMsg(R.string.CARD_SHOW_data_error, "There is no transit arguments.");
            return;
        }

        iTextItem repliedItem = transitArguments.getParcelable(Constants.REPLIED_ITEM);
        commentsPresenter.onReplyClicked(repliedItem);
    }

    private void processCardEditionResult(int resultCode, @Nullable Intent data) {
        try {
            switch (resultCode) {
                case RESULT_OK:
                    Card card = data.getParcelableExtra(Constants.CARD);
                    cardPresenter.onCardEdited(card);
                    break;

                case RESULT_CANCELED:
                    showToast(R.string.CARD_SHOW_edition_is_cancelled);
                    break;

                default:
                    showToast(R.string.unknown_rsult_code);
                    break;
            }
        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_SHOW_data_error, e.getMessage());
        }
    }
}
