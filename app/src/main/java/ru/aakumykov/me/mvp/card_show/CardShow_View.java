package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.card.edit.CardEdit_View;
import ru.aakumykov.me.mvp.comment.CommentsAdapter;
import ru.aakumykov.me.mvp.comment.iComments;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

//TODO: уменьшение изображения

public class CardShow_View extends BaseView implements
        iCardShow.View,
        View.OnClickListener,
//        ListView.OnItemClickListener,
        PopupMenu.OnMenuItemClickListener,
        TagView.OnTagClickListener,
        iComments.commentClickListener
{
    private ListView mainListView;

    private TextView titleView;
    private TextView quoteView;
    private ConstraintLayout imageHolder;
    private ProgressBar imageProgressBar;
    private ImageView imageView;
    private TextView descriptionView;

    private TagContainerLayout tagsContainer;

    private ProgressBar commentsThrobber;

    private LinearLayout commentForm;
    private EditText commentInput;
    private ImageView sendCommentButton;
    private Button addCommentButton;

    private final static String TAG = "CardShow_View";
    private boolean firstRun = true;
    private iCardShow.Presenter presenter;
    private ArrayList<Comment> commentsList;
    private CommentsAdapter commentsAdapter;

    private Card currentCard;
    private Comment currentComment;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        // Собираю разметку из частей
        mainListView = findViewById(R.id.mainListView);
        View headerView = getLayoutInflater().inflate(R.layout.card_show_header, null);
        View footerView = getLayoutInflater().inflate(R.layout.card_show_footer, null);
        mainListView.addHeaderView(headerView);
        mainListView.addFooterView(footerView);

        // Подключаю элементы
        titleView = findViewById(R.id.titleView);
        quoteView = findViewById(R.id.quoteView);
        imageHolder = findViewById(R.id.imageHolder);
        imageProgressBar = findViewById(R.id.imageProgressBar);
        imageView = findViewById(R.id.imageView);
        descriptionView = findViewById(R.id.descriptionView);
        tagsContainer = findViewById(R.id.tagsContainer);

        commentsThrobber = findViewById(R.id.commentsThrobber);

        addCommentButton = findViewById(R.id.addCommentButton);
        commentForm = findViewById(R.id.commentForm);
        commentInput = findViewById(R.id.commentInput);
        sendCommentButton = findViewById(R.id.sendCommentButton);

        // Устанавливаю обработчики нажатий
        addCommentButton.setOnClickListener(this);
        sendCommentButton.setOnClickListener(this);

        // Присоединяю адаптер списка
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, R.layout.comments_list_item, commentsList, this);
        mainListView.setAdapter(commentsAdapter);

//        mainListView.setOnItemClickListener(this);
        tagsContainer.setOnTagClickListener(this);

        presenter = new CardShow_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkView(this);

        if (firstRun) {
            loadCard();
            firstRun = false; // эта строка должна быть ниже loadCard(), так как там тоже проверяется firstRun
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult("+requestCode+", "+requestCode+", ...), "+data);
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this); // обязательно

        if (RESULT_OK == resultCode) {

            switch (requestCode) {

                case Constants.CODE_EDIT_CARD:
                    if (null != data) {
                        Card card = data.getParcelableExtra(Constants.CARD);
                        displayCard(card);
                    } else {
                        showErrorMsg(R.string.error_displaying_card);
                        Log.e(TAG, "Intent data in activity result == null.");
                    }
                    break;

                default:
                    showErrorMsg(R.string.unknown_request_code);
                    Log.d(TAG, "Unknown request code: "+requestCode);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isUserLoggedIn()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.edit, menu);
            menuInflater.inflate(R.menu.delete, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionEdit:
                presenter.editCard();
                break;

            case R.id.actionDelete:
                presenter.deleteCard(currentCard);
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommentButton:
                showCommentForm();
                break;
            case R.id.sendCommentButton:
                sendComment();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//    }

    @Override
    public void onCommentMenuClicked(View view, Comment comment) {
        showCommentMenu(view, comment);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actionEdit:
//                presenter.editComment(currentComment);
                break;
            case R.id.actionDelete:
                presenter.deleteComment(currentComment);
                break;
            case R.id.actionShare:
//                presenter.shareComment(currentComment);
                break;
            default:
                break;
        }
        return true;
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void displayCard(@Nullable final Card card) {
        Log.d(TAG, "displayCard(), "+card);

        // TODO: а где её очищать?
        this.currentCard = card;

        hideProgressBar();
        hideMsg();

        if (null == card) {
            showErrorMsg(R.string.CARD_SHOW_error_displaying_card);
            return;
        }

        String pageTitle = getResources().getString(R.string.CARD_SHOW_page_title, card.getTitle());
        setPageTitle(pageTitle);

        switch (card.getType()) {
            case Constants.IMAGE_CARD:
                displayImageCard(card);
                break;

            case Constants.TEXT_CARD:
                displayTextCard(card);
                break;

            default:
                showErrorMsg(R.string.wrong_card_type);
                break;
        }
    }

    @Override
    public void displayImage(Uri imageURI) {
        Log.d(TAG, "displayImage("+imageURI+")");

        MyUtils.show(imageHolder);
        MyUtils.show(imageProgressBar);
        MyUtils.hide(imageView);

        Picasso.get().load(imageURI).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                MyUtils.hide(imageProgressBar);
                MyUtils.show(imageView);
            }

            @Override
            public void onError(Exception e) {
                showErrorMsg(R.string.error_loading_image, e.getMessage());
                e.printStackTrace();
                MyUtils.hide(imageProgressBar);
                displayImageError();
            }
        });
    }

    @Override
    public void displayImageError() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
    }

    @Override
    public void displayTags(HashMap<String,Boolean> tagsHash) {
//        Log.d(TAG, "displayTags(), "+tagsHash);
        if (null != tagsHash) {
            List<String> tagsList = new ArrayList<>(tagsHash.keySet());
            tagsContainer.setTags(tagsList);
            MyUtils.show(tagsContainer);
        }
    }

    @Override
    public void showCommentsThrobber() {
        MyUtils.show(commentsThrobber);
    }

    @Override
    public void hideCommentsThrobber() {
        MyUtils.hide(commentsThrobber);
    }

    @Override
    public void displayComments(List<Comment> list) {
        commentsList.addAll(list);
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void appendComment(Comment comment) {
        commentsList.add(comment);
        commentsAdapter.notifyDataSetChanged();
//        mainListView.setSelection(commentsList.size()-1);
//        mainListView.setSelection(commentsAdapter.getCount() - 1);
    }

    @Override
    public void removeComment(Comment comment) {
        commentsAdapter.remove(comment);
    }

    // Меток методы
    @Override
    public void onTagClick(int position, String text) {
        Log.d(TAG, "onTagClick("+position+", "+text+")");
        presenter.onTagClicked(text);
    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {

    }


    // Переходы
    @Override
    public void goEditPage(Card card) {
        Log.d(TAG, "goEditPage()");

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD_KEY, card.getKey());

        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    @Override
    public void goList(@Nullable String tagFilter) {
        Intent intent = new Intent(this, CardsList_View.class);
        if (null != tagFilter)
            intent.putExtra(Constants.TAG_FILTER, tagFilter);
        startActivity(intent);
    }


    // Диалоги
    @Override
    public void showCardDeleteDialog() {

        YesNoDialog yesNoDialog = new YesNoDialog(
                this,
                R.string.DIALOG_card_deletion,
                R.string.DIALOG_really_delete_card,
                new iDialogCallbacks.Delete() {
                    @Override
                    public boolean deleteDialogCheck() {
                        // TODO: можно же здесь делать
                        // что-то вроде presenter.isAdmin()...
                        return true;
                    }

                    @Override
                    public void deleteDialogYes() {
                        try {
                            presenter.onCardDeleteConfirmed(currentCard);
                        } catch (Exception e) {
                            showErrorMsg(R.string.CARD_SHOW_error_deleting_card);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDeleteDialogNo() {

                    }
                }
        );

        yesNoDialog.show();
    }

    @Override
    public void showCommentEditDialog(Comment comment) {

    }

    @Override
    public void showCommentDeleteDialog(final Comment comment) {

        YesNoDialog yesNoDialog = new YesNoDialog(
                this,
                R.string.COMMENT_comment_deletion,
                MyUtils.cutToLength(comment.getText(), Constants.COMMENT_DELETE_DIALOG_TEXT_LENGTH),
                new iDialogCallbacks.Delete() {
                    @Override
                    public boolean deleteDialogCheck() {
                        return true;
                    }

                    @Override
                    public void deleteDialogYes() {
                        try {
                            presenter.onCommentDeleteConfirmed(comment);
                        } catch (Exception e) {
                            showErrorMsg(R.string.COMMENT_delete_error, e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDeleteDialogNo() {

                    }
                }
        );

        yesNoDialog.show();
    }


    // Вспомогательные
    @Override
    public void enableCommentForm() {
        MyUtils.enable(commentInput);
        MyUtils.enable(sendCommentButton);
    }

    @Override
    public void disableCommentForm() {
        MyUtils.disable(commentInput);
        MyUtils.disable(sendCommentButton);
    }

    @Override
    public void resetCommentForm() {
        commentInput.setText(null);
        enableCommentForm();
    }


    // Внутренние методы
    private void loadCard() {
        if (firstRun) {
            try {
                presenter.processInputIntent(getIntent());
            } catch (Exception e) {
                hideProgressBar();
                showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
                e.printStackTrace();
            }
            firstRun = false;
        }
    }

    private void displayImageCard(Card card) {
        Log.d(TAG, "displayImageCard(), "+card);

        displayCommonCard(card);

        try {
            Uri imageURI = Uri.parse(card.getImageURL());
            displayImage(imageURI);
        } catch (Exception e) {
            displayImageError();
        }
    }

    private void displayTextCard(Card card) {
        Log.d(TAG, "displayTextCard(), "+card);
        quoteView.setText(card.getQuote());
        MyUtils.show(quoteView);
        displayCommonCard(card);
    }

    private void displayCommonCard(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        displayTags(card.getTags());
    }

    private View constructCommentItem(Comment comment) throws Exception {

        LinearLayout commentRow = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.comments_list_item, null);

        ((TextView) commentRow.findViewById(R.id.commentText))
                .setText(comment.getText());

        commentRow.findViewById(R.id.commentReply)
                .setTag(Comment.key_commentId, comment.getKey());

        return commentRow;
    }

    private void showCommentForm() {
        MyUtils.hide(addCommentButton);
        MyUtils.show(commentForm);
        commentInput.requestFocus();
        MyUtils.showKeyboard(this, commentInput);
    }

    private void sendComment() {
//        commentInput.clearFocus();
//        MyUtils.hideKeyboard(this, commentInput);

        disableCommentForm();

        String commentText = commentInput.getText().toString();
        presenter.postComment(commentText);
    }

    private void showCommentMenu(final View v, final Comment comment) {

        currentComment = comment;

        PopupMenu popupMenu = new PopupMenu(this, v);

        // TODO: сделать это по-нормальному
        // TODO: логика-то во вьюхе не должна присутствовать!
        if (isUserLoggedIn()) {
            if (comment.getUserId().equals(getAuthService().currentUid()))
                popupMenu.inflate(R.menu.edit);

            if (getAuthService().isAdmin(getAuthService().currentUid()))
                popupMenu.inflate(R.menu.delete);
        }

        popupMenu.inflate(R.menu.share);

//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu popupMenu) {
//                v.setBackground(oldBackground);
//            }
//        });

        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.setGravity(Gravity.END);

        popupMenu.show();
    }

//    @OnClick(R.id.commentReply)
//    void replyToTomment(View view) {
////        presenter.replyToComment(view.getTag(Comment.key_commentId));
//    }
//
}
