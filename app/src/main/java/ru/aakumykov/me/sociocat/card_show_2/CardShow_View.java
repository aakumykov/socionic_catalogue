package ru.aakumykov.me.sociocat.card_show_2;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show_2.services.Card_Service;
import ru.aakumykov.me.sociocat.card_show_2.services.Comments_Service;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardShow_View extends AppCompatActivity implements
        iCardController,
        iCommentsController
{
    public interface LoadCommentsCallbacks {
        void onLoadCommentsSuccess(List<Comment> list);
        void onLoadCommentsFail(String errorMsg);
    }

    private final static String TAG ="CardShow_View";

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
    private ArrayList<Item> list;
    private boolean flagCommentsLoadInProgress = false;
    private Comments_Service commentsService;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_show);
        ButterKnife.bind(this);

        list = new ArrayList<>();
        dataAdapter = new DataAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dataAdapter);
        //setupAutoCommentsLoading();

        commentsService = new Comments_Service();

        //loadCard();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.change, menu);
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionChange:
                refreshCard();
                break;

            case R.id.actionEdit:
                startEditCard();
                break;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (View.VISIBLE == commentFormContainer.getVisibility())
            hideCommentForm();
        else
            super.onBackPressed();
    }


    // Интерфейсные методы
    @Override
    public Context getContext() {
        return (Context) this;
    }

    @Override
    public void likeCard(Card card, LikeCardCallbacks cardCallbacks) {

    }

    @Override
    public void loadComments(int startAfter, int count) {

        if (!flagCommentsLoadInProgress) {

            flagCommentsLoadInProgress = true;
            dataAdapter.hideLoadMoreItem();
            dataAdapter.showCommentsThrobber();

            new Comments_Service().loadComments(startAfter + 1, count, new Comments_Service.iLoadCommentsCallbacks() {
                @Override
                public void onCommentsLoadSuccess(List<Comment> list) {
                    dataAdapter.hideCommentsThrobber();
                    dataAdapter.appendComments(list);
                    flagCommentsLoadInProgress = false;
                }

                @Override
                public void onCommentsLoadFail(String errorMsg) {
                    showError(errorMsg);
                    flagCommentsLoadInProgress = false;
                }
            });
        }
    }

    @Override
    public void editComment(Comment comment) {
        MyUtils.showToast(this, "Правка комментария «"+comment.getText()+"»");
    }


    // Внешние методы
    public void scrollToComment(Comment comment) {
        int position = dataAdapter.findCommentPosition(comment);
        if (-1 != position)
            recyclerView.scrollToPosition(position);
        else
            MyUtils.showToast(this, "Комментарий не найден");
    }

    public void showCommentForm(Item parentItem) {
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

    public void hideCommentForm() {
        hideParentCommentPiece();
        commentInput.setText("");
        commentFormContainer.setVisibility(View.GONE);
    }

    private void enableCommentForm() {
        commentInput.setEnabled(true);
        sendCommentWidget.setEnabled(true);
    }

    private void disableCommentForm() {
        commentInput.setEnabled(false);
        sendCommentWidget.setEnabled(false);
    }


    // Нажатия
    @OnClick(R.id.parentCommentDiscardWidget)
    void hideParentCommentPiece() {
        parentCommentTextView.setText("");
        parentCommentContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.sendCommentWidget)
    void postComment() {

        /*Comment comment = new Comment(commentInput.getText().toString());
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
        });*/
    }


    // Внутренние методы
    private void loadCard() {
        showProgressMessage("Загрузка карточки");

        new Card_Service().loadCard("", new Card_Service.iCardLoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                hideProgressMessage();
                dataAdapter.setCard(card);
                loadComments(0, 10);
            }

            @Override
            public void onCardLoadFail(String errorMsg) {
                showError(errorMsg);
            }
        });
    }

    private void refreshCard() {
        dataAdapter.clearList();
        loadCard();
        //loadComments(0, 30);
    }

    private void startEditCard() {
        MyUtils.showToast(this, "Правка карточки...");
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressMessage(String msg) {
        messageView.setText(msg);
        messageView.setVisibility(View.VISIBLE);
        showProgressBar();
    }

    private void hideProgressMessage() {
        messageView.setVisibility(View.GONE);
        hideProgressBar();
    }

    private void showError(String errorMsg) {
        hideProgressBar();
        dataAdapter.hideCommentsThrobber();

        messageView.setText(errorMsg);
        messageView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        messageView.setVisibility(View.GONE);
    }

    private void setupAutoCommentsLoading() {
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    loadComments(dataAdapter.getLastComment().getKey(), 10);
                }
            }
        });*/
    }
}
