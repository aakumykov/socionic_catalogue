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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.view_model.CardShow_ViewModel;
import ru.aakumykov.me.sociocat.card_show.view_model.CardShow_ViewModel_Factory;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.comment_form.CommentForm;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public class CardShow_View extends BaseView implements
        iCardShow.iPageView
{
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.commentFormContainer) FrameLayout commentFormContainer;

    private final static String TAG = "CardShow_View";
    private boolean firstRun = true;
    private iCardShow.iDataAdapter dataAdapter;
    private iCardShow.iPresenter presenter;
    private iCommentForm commentForm;

    private CardShow_ViewModel cardShowViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);

        cardShowViewModel = new ViewModelProvider(this, new CardShow_ViewModel_Factory()).get(CardShow_ViewModel.class);

        this.presenter = cardShowViewModel.getPresenter();
        if (null == this.presenter) {
            this.presenter = new CardShow_Presenter();
            cardShowViewModel.storePresenter(this.presenter);
        }

        this.dataAdapter = cardShowViewModel.getDataAdapter();
        if (null == this.dataAdapter) {
            this.dataAdapter = new DataAdapter(presenter);
            cardShowViewModel.storeDataAdapter(this.dataAdapter);
        }

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);

        this.commentForm = new CommentForm(this, commentFormContainer);
        this.commentForm.addButtonListeners(new iCommentForm.ButtonListeners() {
            @Override
            public void onRemoveQuoteClicked() {
                presenter.onRemoveCommentQuoteClicked();
            }

            @Override
            public void onSendCommentClicked(String commentText) {
                presenter.onSendCommentClicked();
            }
        });

        configureSwipeRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.bindViewAndAdapter(this, dataAdapter);

        switch (requestCode) {
            case Constants.CODE_LOGIN_REQUEST:
                processLoginRequest(resultCode, data);
                break;
            case Constants.CODE_EDIT_CARD:
                processCardEditionResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.bindViewAndAdapter(this, dataAdapter);

        if (!dataAdapter.isFilled())
            presenter.processInputIntent(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindViewAndAdapter();
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
                presenter.onEditCardClicked();
                break;
            case R.id.actionDelete:
                presenter.onDeleteCardClicked();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (commentForm.isVisible())
            commentForm.hide();
        else
            super.onBackPressed();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void hideSwipeThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showCommentForm(Comment editedComment) {
        commentForm.setText(editedComment.getText());
        commentForm.show();
    }

    @Override
    public void showCommentForm(iCommentable repliedItem) {
        if (repliedItem instanceof Comment) {
            Comment repliedComment = (Comment) repliedItem;
            commentForm.setQuote(repliedComment.getText());
        }
        commentForm.show();
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
    public void showCardsWithTag(String tagName) {
        Intent intent = new Intent(this, CardsGrid_View.class);
        intent.setAction(Constants.ACTION_SHOW_CARDS_WITH_TAG);
        intent.putExtra(Constants.TAG_NAME, tagName);
        startActivity(intent);
    }

    @Override
    public void goEditCard(Card card) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    // Установка результата для Плиточного вида, чтобы там обновилась отредактированная карточка
    @Override
    public void setSuccessEditionResult(Card card) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD, card);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public String getCommentText() {
        return this.commentForm.getText();
    }


    // Внутренние методы
    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.onRefreshRequested();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);
    }

    private void processInputIntent() {
        Intent intent = getIntent();

        try {
            Card card = intent.getParcelableExtra(Constants.CARD);
            // TODO: перенести в Card_ViewHolder
            presenter.onPageOpened(card.getKey());
        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void processLoginRequest(int resultCode, @Nullable Intent data) {
        // TODO: переделать это на работу через исключения

        if (RESULT_CANCELED == resultCode) {
            return;
        }

        if (RESULT_OK != resultCode) {
            showToast(R.string.LOGIN_login_error);
            return;
        }

        if (null == data) {
            showToast(R.string.CARD_SHOW_data_error);
            return;
        }

        Intent transitIntent = data.getParcelableExtra(Constants.TRANSIT_INTENT);
        if (null == transitIntent) {
            showToast(R.string.CARD_SHOW_data_error);
            return;
        }

        try {
            presenter.processLoginRequest(transitIntent);
        }
        catch (IllegalArgumentException e) {

        }
    }

    // TODO: этому место в Presenter-е
    private void processCardEditionResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (null != data) {
                    Card card = data.getParcelableExtra(Constants.CARD);
                    presenter.onEditCardComplete(card);
                }
                else {
                    showErrorMsg(R.string.data_error, "Edited card is null");
                }
                break;
            case RESULT_CANCELED:
                showToast(R.string.CARD_SHOW_edit_cancelled);
                break;
            default:
                break;
        }
    }
}
