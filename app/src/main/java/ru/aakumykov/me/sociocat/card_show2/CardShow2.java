package ru.aakumykov.me.sociocat.card_show2;

import android.content.Intent;
import android.os.Bundle;
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
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public class CardShow2 extends BaseView implements
        iCardShow2.iPageView
{
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private final static String TAG = "CardShow2";
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
    protected void onStart() {
        super.onStart();

        presenter.bindView(this);
        presenter.bindDataAdapter(dataAdapter);

        if (firstRun) {
            firstRun = false;
            processInputIntent();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindDataAdapter();
        presenter.unbindView();
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
