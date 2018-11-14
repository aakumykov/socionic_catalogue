package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.card.edit.CardEdit_View;
import ru.aakumykov.me.mvp.comment.CommentsAdapter;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Card;

//TODO: уменьшение изображения

public class CardShow_View extends BaseView implements
        iCardShow.View,
        TagView.OnTagClickListener
{
    @BindView(R.id.scrollView) ScrollView scrollView;
    @BindView(R.id.cardHolder) LinearLayout cardHolder;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.descriptionView) TextView descriptionView;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

//    @BindView(R.id.commentsHolder) LinearLayout commentsHolder;
//    @BindView(R.id.newCommentForm) LinearLayout newCommentForm;
//    @BindView(R.id.newCommentInput) EditText newCommentInput;
//    @BindView(R.id.newCommentSend) ImageView newCommentSend;
//    @BindView(R.id.addCommentButton) Button addCommentButton;

    @BindView(R.id.commentReply) TextView commentReply;


    private final static String TAG = "CardShow_View";
    private iCardShow.Presenter presenter;
    private boolean firstRun = true;

    private ArrayList<Comment> commentsList;
    private CommentsAdapter commentsAdapter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_show2);
        ButterKnife.bind(this);

        ListView listView = findViewById(R.id.cardMainListView);
        View headerView = getLayoutInflater().inflate(R.layout.card_show_header, null);
        listView.addHeaderView(headerView);

        tagsContainer.setOnTagClickListener(this);

//        super.onCreate(savedInstanceState);

        presenter = new CardShow_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        if (firstRun) {
            loadCard();
            firstRun = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        MyUtils.hideKeyboard(this, newCommentInput);
//    }

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
            menuInflater.inflate(R.menu.edit_delete, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionEdit:
                presenter.onEditButtonClicked();
                break;

            case R.id.actionDelete:
                presenter.onDeleteButtonClicked();
                break;

            default:
                super.onOptionsItemSelected(item);
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
    public void displayCard(@Nullable Card card) {
        Log.d(TAG, "displayCard(), "+card);

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
    public void displayComments(List<Comment> list) {

//        MyUtils.show(commentsHolder);

        for (Comment aComment : list)
            appendComment(aComment);
    }

    @Override
    public void appendComment(Comment comment) {
        try {
            View commentRow = constructCommentItem(comment);
//            commentsHolder.addView(commentRow);
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_SHOW_error_displaying_comments);
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
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
    public void showDeleteDialog() {

//        YesNoDialog yesNoDialog = new YesNoDialog(
//                this,
//                R.string.DIALOG_card_deletion,
//                R.string.DIALOG_really_delete_card,
//                new iDialogCallbacks.onCheck() {
//                    @Override
//                    public boolean doCheck() {
//                        return true;
//                    }
//                },
//                new iDialogCallbacks.onYes() {
//                    @Override
//                    public void yesAction() {
//                        //Log.d(TAG, "yesAction");
//                        presenter.onDeleteConfirmed();
//                    }
//                },
//                new iDialogCallbacks.onNo() {
//                    @Override
//                    public void noAction() {
//                        //Log.d(TAG, "noAction");
//                    }
//                }
//        );
//
//        yesNoDialog.show();
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


    // Нажатия
//    @OnClick(R.id.addCommentButton)
//    void activateEditText() {
//        MyUtils.hide(addCommentButton);
//        MyUtils.show(newCommentForm);
//        newCommentInput.requestFocus();
//        MyUtils.showKeyboard(this, newCommentInput);
//
////        scrollView.post(new Runnable() {
////            @Override
////            public void run() {
////                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
////            }
////        });
//    }
//
//    @OnClick(R.id.newCommentSend)
//    void sendComment() {
//        String commentText = newCommentInput.getText().toString();
//        presenter.postComment(commentText);
//    }
//
//    @OnClick(R.id.commentReply)
//    void replyToTomment(View view) {
////        presenter.replyToComment(view.getTag(Comment.key_commentId));
//    }
//
//
//    // Другое
//    @Override
//    public void disableCommentForm() {
////        MyUtils.hideKeyboard(this, newCommentInput);
//        MyUtils.disable(newCommentInput);
//        MyUtils.disable(newCommentSend);
//    }
//
//    @Override
//    public void enableCommentForm() {
//        MyUtils.enable(newCommentInput);
//        MyUtils.enable(newCommentSend);
//    }
//
//    @Override
//    public void resetCommentForm() {
//        newCommentInput.setText(null);
//        enableCommentForm();
////        MyUtils.hideKeyboard(this, newCommentInput);
//    }
}
