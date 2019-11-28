package ru.aakumykov.me.sociocat.card_show2;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.iCommentable;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public class CardShow2_View extends BaseView implements
        iCardShow2.iPageView
{
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private final static String TAG = "CardShow2_View";
    private boolean firstRun = true;
    private iCardShow2.iDataAdapter dataAdapter;
    private iCardShow2.iPresenter presenter;
    private iCommentForm commentForm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);

        this.presenter = new CardShow2_Presenter();
        this.dataAdapter = new DataAdapter(presenter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);

        this.commentForm = new CommentForm(this, commentFormContainer);
        this.commentForm.addButtonListeners(new iCommentForm.ButtonListeners() {
            @Override
            public void onRemoveQuoteClicked() {

            }

            @Override
            public void onSendCommentClicked(String commentText) {
                presenter.onSendCommentClicked();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.bindViewAndAdapter(this, dataAdapter);

        switch (requestCode) {
            case Constants.CODE_LOGIN_REQUEST:
                processLoginRequest(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processLoginRequest(int resultCode, @Nullable Intent data) {
        if (RESULT_OK != resultCode && RESULT_CANCELED != resultCode) {
            showToast(R.string.LOGIN_login_error);
            return;
        }

        if (null == data) {
            showToast(R.string.CARD_SHOW_data_error);
            return;
        }

        Bundle transitArguments = data.getBundleExtra(Constants.TRANSIT_ARGUMENTS);
        if (null == transitArguments) {
            showToast(R.string.CARD_SHOW_data_error);
            return;
        }

        String replyAction = transitArguments.getString(iCardShow2.REPLY_ACTION, "");
        iCommentable repliedObject = transitArguments.getParcelable(iCardShow2.REPLIED_OBJECT);
        presenter.processLoginRequest(replyAction, repliedObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        if (presenter.canEditCard())
            menuInflater.inflate(R.menu.edit, menu);

        if (presenter.canDeleteCard())
            menuInflater.inflate(R.menu.delete, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionEdit:
                showToast(R.string.not_implemented_yet);
                break;
            case R.id.actionDelete:
                showToast(R.string.not_implemented_yet);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.bindViewAndAdapter(this, dataAdapter);

        if (firstRun) {
            firstRun = false;
            processInputIntent();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindViewAndAdapter();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Внутренние методы
    private void processInputIntent() {
        Intent intent = getIntent();

        try {
            Card card = intent.getParcelableExtra(Constants.CARD);
            presenter.onPageOpened(card.getKey());
        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    @Override
    public void showCommentForm() {
        this.commentForm.show(false);
    }

    @Override
    public void hideCommentForm() {
        this.commentForm.hideError();
        this.commentForm.hide();
    }

    @Override
    public void disableCommentForm() {
        this.commentForm.disable();
    }

    @Override
    public void clearCommentForm() {
        this.commentForm.clear();
    }

    @Override
    public void showCommentFormError(int errorMessageId, String errorMsg) {
        this.commentForm.enable();
        this.commentForm.showError(errorMessageId, errorMsg);
    }

    @Override
    public void scrollToComment(int position) {
        recyclerView.scrollToPosition(position);
//        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public String getCommentText() {
        return this.commentForm.getText();
    }
}