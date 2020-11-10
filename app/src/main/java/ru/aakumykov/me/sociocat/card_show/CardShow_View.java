package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.view_model.CardShow_ViewModel;
import ru.aakumykov.me.sociocat.card_show.view_model.CardShow_ViewModel_Factory;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.iCommentable;
import ru.aakumykov.me.sociocat.user_show.UserShow_View;
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
            this.dataAdapter = new CardShow_DataAdapter(presenter);
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

        Intent intent = getIntent();
        String action = (null != intent) ? intent.getAction() : null;

        if (dataAdapter.notYetFilled())
            presenter.onFirstOpen(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindViewAndAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        //menuInflater.inflate(R.menu.open_in_browser, menu);

        if (presenter.canEditCard())
            menuInflater.inflate(R.menu.card_edit, menu);

        if (presenter.canDeleteCard())
            menuInflater.inflate(R.menu.card_delete, menu);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.onGoBackRequested();
                break;

            case R.id.actionEdit:
                presenter.onEditCardClicked();
                break;

            case R.id.actionDelete:
                presenter.onDeleteCardClicked();
                break;

            case R.id.actionOpenInBrowser:
                presenter.onOpenInBrowserClicked();
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
        else {
            presenter.onGoBackRequested();
        }
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

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

    @Override
    public void goUserProfile(String userId) {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void openImageInBrowser(String imageURL) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(imageURL));
        startActivity(intent);
    }

    @Override
    public void goBack(@NonNull Card card) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, card);
        intent.setAction(Intent.ACTION_VIEW);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
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
                presenter.onEditCardComplete(data);
                break;
            case RESULT_CANCELED:
                showToast(R.string.CARD_SHOW_edit_cancelled);
                break;
            default:
                break;
        }
    }
}
