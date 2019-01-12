package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
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
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.edit.CardEdit_View;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.comment.CommentsAdapter;
import ru.aakumykov.me.mvp.comment.iComments;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.login.Login_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.users.edit.UserEdit_View;
import ru.aakumykov.me.mvp.users.show.UserShow_View;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

//TODO: уменьшение изображения

public class CardShow_View extends BaseView implements
        iCardShow.View,
        View.OnClickListener,
        PopupMenu.OnMenuItemClickListener,
        TagView.OnTagClickListener,
        iComments.commentClickListener
{
    private ListView mainListView;

    private LinearLayout commentLayout;

    private TextView titleView;
    private TextView quoteView;
    private ConstraintLayout imageHolder;
    private ProgressBar imageProgressBar;
    private ImageView imageView;
    private TextView descriptionView;

    private TextView authorView;

    private TagContainerLayout tagsContainer;
    private TextView cardRatingView;
    private ImageView cardRateUpButton;
    private ImageView cardRateDownButton;
    private ProgressBar cardRatingThrobber;

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
    private Comment parentComment;
    private View currentCommentView;

    private ProgressBar videoPlayerThrobber;
    private FrameLayout videoPlayerHolder;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;


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

        // Подключаю элементы интерфейса
        commentLayout = findViewById(R.id.commentLayout);
        titleView = findViewById(R.id.titleView);
        quoteView = findViewById(R.id.quoteView);

        imageHolder = findViewById(R.id.imageHolder);
        imageProgressBar = findViewById(R.id.imageProgressBar);
        imageView = findViewById(R.id.imageView);

        videoPlayerThrobber = findViewById(R.id.videoPlayerThrobber);
        videoPlayerHolder = findViewById(R.id.videoPlayerHolder);

        descriptionView = findViewById(R.id.descriptionView);
        authorView = findViewById(R.id.authorView);

        tagsContainer = findViewById(R.id.tagsContainer);

        cardRatingView = findViewById(R.id.cardRatingView);
        cardRateUpButton = findViewById(R.id.cardRateUpButton);
        cardRateDownButton = findViewById(R.id.cardRateDownButton);
        cardRatingThrobber = findViewById(R.id.cardRatingThrobber);

        commentsThrobber = findViewById(R.id.commentsThrobber);
        addCommentButton = findViewById(R.id.addCommentButton);
        commentForm = findViewById(R.id.commentForm);
        commentInput = findViewById(R.id.commentInput);
        sendCommentButton = findViewById(R.id.sendCommentButton);

        // Устанавливаю обработчики нажатий
        authorView.setOnClickListener(this);
        addCommentButton.setOnClickListener(this);
        sendCommentButton.setOnClickListener(this);
        cardRateUpButton.setOnClickListener(this);
        cardRateDownButton.setOnClickListener(this);

        // Присоединяю адаптер списка
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, auth().currentUser(),
                R.layout.comments_list_item, commentsList,this);
        mainListView.setAdapter(commentsAdapter);

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
    protected void onDestroy() {
        super.onDestroy();
        if (null != youTubePlayerView)
            youTubePlayerView.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this); // обязательно

        switch (requestCode) {

            case Constants.CODE_EDIT_CARD:
                processEditionResult(resultCode, data);
                break;

            case Constants.CODE_LOGIN_FOR_COMMENT:
                processLoginForComment(resultCode, data);
                break;

            case Constants.CODE_FORCE_SETUP_USER_NAME:
                if (null != data) {
                    User user = data.getParcelableExtra(Constants.USER);
                    auth().storeCurrentUser(user);
                }
                addComment();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        invalidateOptionsMenu();

        menuInflater.inflate(R.menu.share, menu);

        if (null != currentCard) {
            if (auth().isAdmin() || auth().isCardOwner(currentCard)) {
                menuInflater.inflate(R.menu.edit, menu);
                menuInflater.inflate(R.menu.delete, menu);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        MenuInflater menuInflater = getMenuInflater();
//
//        if (auth().isAdmin() || auth().isCardOwner(currentCard)) {
//            menuInflater.inflate(R.menu.edit, menu);
//            menuInflater.inflate(R.menu.delete, menu);
//        }
//
//        menuInflater.inflate(R.menu.share, menu);
//
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionEdit:
                editCard();
                break;

            case R.id.actionDelete:
                deleteCard();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.authorView:
                showAuthor();
                break;

            case R.id.addCommentButton:
                addComment();
                break;

            case R.id.sendCommentButton:
                sendComment();
                break;

            case R.id.cardRateUpButton:
                presenter.rateCardUp();
                break;

            case R.id.cardRateDownButton:
                presenter.rateCardDown();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCommentMenuClicked(View view, Comment comment) {
        showCommentMenu(view, comment);
    }

    @Override
    public void onCommentReplyClicked(View view, final Comment comment) {
        // TODO: эта логика должна быть в презентере

        if (!auth().isUserLoggedIn()) {
            showToast(R.string.DIALOG_login_first);
        }
        else if (forceSetupUserName(comment)) {
            parentComment = comment;
            showCommentForm();
        }
    }

    // TODO: КРИВО!!! Сюда влезла служба!
    @Override
    public void onCommentRateUpClicked(final Comment comment) {
        final Comment oldComment = comment;

        presenter.rateCommentUp(comment, new iCommentsSingleton.RatingCallbacks() {
            @Override
            public void onRetedUp(Comment comment) {
                int index = commentsList.indexOf(oldComment);
                commentsList.set(index, comment);
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRatedDown(Comment comment) {
                // не используется
            }

            @Override
            public void onRateFail(String errorMsg) {

            }
        });
    }

    @Override
    public void onCommentRateDownClicked(final Comment comment) {
        final Comment oldComment = comment;

        presenter.rateCommentDown(comment, new iCommentsSingleton.RatingCallbacks() {
            @Override
            public void onRetedUp(Comment comment) {
                // не используется
            }

            @Override
            public void onRatedDown(Comment comment) {
                int index = commentsList.indexOf(oldComment);
                commentsList.set(index, comment);
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRateFail(String errorMsg) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.actionEdit:
                editComment();
                break;

            case R.id.actionDelete:
                deleteComment();
                break;

            case R.id.actionShare:
//                presenter.shareComment(currentComment);
                break;

            default:
                break;
        }

        return true;
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        int videoPosition = youTubePlayer.getCurrentTimeMillis();
//        Log.d("STATE SAVE", "videoPosition: "+videoPosition);
//        outState.putInt("videoPosition", videoPosition);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        int videoPosition = savedInstanceState.getInt("videoPosition");
//        Log.d("STATE RESTORE", "videoPosition: "+videoPosition);
////        youTubePlayer.seekToMillis(videoPosition);
//    }


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

            case Constants.VIDEO_CARD:
                displayVideoCard(card);
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
        MyUtils.show(imageView);
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


    @Override
    public void showCardRatingThrobber() {
        MyUtils.hide(cardRatingView);
        MyUtils.show(cardRatingThrobber);
    }

    @Override
    public void showCardRating(int value) {
        MyUtils.hide(cardRatingThrobber);
        MyUtils.show(cardRatingView);
        cardRatingView.setText(String.valueOf(value));

        String currentUserId = auth().currentUserId();

        if (currentCard.isRatedUpBy(currentUserId)) {
            colorizeCardRatingAsUp();
        }

        if (currentCard.isRatedDownBy(currentUserId)) {
            colorizeCardRatingAsDown();
        }
    }

    @Override
    public void onCardRatedUp(int newRating) {
        showCardRating(newRating);
        colorizeCardRatingAsUp();
    }

    @Override
    public void onCardRatedDown(int newRating) {
        showCardRating(newRating);
        colorizeCardRatingAsDown();
    }

//    @Override
//    public void onCardRatedUp(int newRating) {
//        Drawable upColoredIcon = getResources().getDrawable(R.drawable.ic_thumb_up_colored);
//        Drawable downNeutralIcon = getResources().getDrawable(R.drawable.ic_thumb_down_neutral);
//        cardRateUpButton.setImageDrawable(upColoredIcon);
//        cardRateDownButton.setImageDrawable(downNeutralIcon);
//        showCardRating(newRating);
//    }
//
//    @Override
//    public void onCardRatedDown(int newRating) {
//        Drawable downColoredIcon = getResources().getDrawable(R.drawable.ic_thumb_down_colored);
//        Drawable upNeutralIcon = getResources().getDrawable(R.drawable.ic_thumb_up_neutral);
//        cardRateDownButton.setImageDrawable(downColoredIcon);
//        cardRateUpButton.setImageDrawable(upNeutralIcon);
//        showCardRating(newRating);
//    }

    @Override
    public void onCardRateError() {
        showToast(R.string.CARD_SHOW_rating_update_error);
        MyUtils.hide(cardRatingThrobber);
        MyUtils.show(cardRatingView);
    }


    // Меток методы
    @Override
    public void onTagClick(int position, String text) {
        goList(text);
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

//        Intent intent = new Intent(this, CardEdit_View.class);
//        intent.setAction(Constants.ACTION_EDIT);
//        intent.putExtra(Constants.CARD_KEY, card.getKey());
//        startActivityForResult(intent, Constants.CODE_EDIT_CARD);

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
        currentComment = null;
        currentCommentView = null;
        parentComment = null;

        commentInput.setText(null);

        enableCommentForm();
    }

    @Override
    public void showCommentInProgress() {
        if (null != currentCommentView) currentCommentView.setAlpha(0.5f);
    }

    @Override
    public void hideCommentInProgress() {
        if (null != currentCommentView) currentCommentView.setAlpha(1.0f);
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

        MyUtils.show(imageHolder);
        displayCommonCardParts(card);

        try {
            Uri imageURI = Uri.parse(card.getImageURL());
            displayImage(imageURI);
        } catch (Exception e) {
            displayImageError();
        }
    }

    private void displayTextCard(Card card) {
        SpannableString spannableQuote = MVPUtils.aspects2images(this, card.getQuote());

        quoteView.setText(spannableQuote);

        MyUtils.show(quoteView);

        displayCommonCardParts(card);
    }

    private void displayVideoCard(final Card card) {
        displayCommonCardParts(card);

        int playerWidth = MyUtils.getScreenWidth(this);
        int playerHeight = Math.round(MyUtils.getScreenWidth(this) * 9/16);

        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setMinimumWidth(playerWidth);
        youTubePlayerView.setMinimumHeight(playerHeight);

        videoPlayerHolder.addView(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer = initializedYouTubePlayer;
                        youTubePlayer.cueVideo(card.getVideoCode(), 0.0f);
                        MyUtils.show(youTubePlayerView);
                    }
                });
            }
        }, true);
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        authorView.setText( getString(R.string.CARD_SHOW_author, card.getUserName()));

        displayTags(card.getTags());
        showCardRating(card.getRating());

//        if (auth().isUserLoggedIn()) {
            MyUtils.show(addCommentButton);
//        }
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

    private void addComment() {

        if (auth().isUserLoggedIn()) {

            final User user = auth().currentUser();

            if (!TextUtils.isEmpty(user.getName())) {
                showCommentForm();
            } else {
                MyDialogs.userNameRequiredDialog(this, new iMyDialogs.StandardCallbacks() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        Intent intent = new Intent(CardShow_View.this, UserEdit_View.class);
                        intent.putExtra(Constants.USER_ID, user.getKey());
                        startActivityForResult(intent, Constants.CODE_FORCE_SETUP_USER_NAME);
                    }
                });
            }
        }
        else {
            MyDialogs.loginRequiredDialog(this, new iMyDialogs.StandardCallbacks() {
                @Override
                public void onCancelInDialog() {

                }

                @Override
                public void onNoInDialog() {

                }

                @Override
                public boolean onCheckInDialog() {
                    return true;
                }

                @Override
                public void onYesInDialog() {
                    Intent intent = new Intent(CardShow_View.this, Login_View.class);
                    intent.setAction(Constants.ACTION_LOGIN_FOR_COMMENT);
                    startActivityForResult(intent, Constants.CODE_LOGIN_FOR_COMMENT);
                }
            });
        }
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

        if (null != parentComment) {
            presenter.postCommentReply(commentText, parentComment);
        } else {
            presenter.postComment(commentText);
        }
    }

    private void showCommentMenu(final View v, final Comment comment) {

        currentComment = comment;
        currentCommentView = v;

        PopupMenu popupMenu = new PopupMenu(this, v);

        // TODO: сделать это по-нормальному
        // TODO: логика-то во вьюхе не должна присутствовать!
        if (auth().isUserLoggedIn()) {
            if (comment.getUserId().equals(auth().currentUserId()))
                popupMenu.inflate(R.menu.edit);

            if (auth().userIsAdmin(auth().currentUserId()))
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

    private void editCard() {
        goEditPage(currentCard);
    }

    private void deleteCard() {

        MyDialogs.cardDeleteDialog(
                this,
                currentCard.getTitle(),
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        presenter.cardDeleteConfirmed(currentCard);
                    }
                }
        );

    }

    private void editComment() {
        MyDialogs.commentEditDialog(
                this,
                currentComment.getText(),
                new iMyDialogs.StringInputCallback() {
                    @Override
                    public void onDialogWithStringYes(String text) {
                        currentComment.setText(text);
                        presenter.editCommentConfirmed(currentComment);
                    }
                }
        );
    }

    private void deleteComment() {

        String commentPiece = MyUtils.cutToLength(currentComment.getText(), Constants.DIALOG_MESSAGE_LENGTH);
        String message = getString(R.string.COMMENT_delete_message_template, commentPiece);

        MyDialogs.commentDeleteDialog(
                this,
                message,
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        presenter.deleteCommentConfirmed(currentComment);
                    }
                }
        );
    }

    private boolean forceSetupUserName(@Nullable final Comment parentComment) {

        final User user = auth().currentUser();

        if (null != user && !TextUtils.isEmpty(user.getName())) {
            return true;
        }
        else {

            String message = getString(R.string.DIALOG_setup_user_name);

            MyDialogs.goToPageDialog(this, message, new iMyDialogs.StandardCallbacks() {
                @Override
                public void onCancelInDialog() {

                }

                @Override
                public void onNoInDialog() {

                }

                @Override
                public boolean onCheckInDialog() {
                    return true;
                }

                @Override
                public void onYesInDialog() {
                    Intent intent = new Intent(CardShow_View.this, UserEdit_View.class);
                    String userId = user.getKey();
                    intent.putExtra(Constants.USER_ID, userId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivityForResult(intent, Constants.CODE_FORCE_SETUP_USER_NAME);
                }
            });

            return false;
        }
    }

    private void proceedPostComment(Intent data) {

        if (null == data) {
            // TODO: Исключение бы сюда... или это предсказуемые данные?
            showErrorMsg(R.string.data_error, "Intent data is null");
            return;
        }

        this.parentComment = data.getParcelableExtra(Constants.PARENT_COMMENT);
        showCommentForm();
    }

    private void colorizeCardRatingAsUp() {
//        Drawable upNeutralIcon = getResources().getDrawable(R.drawable.ic_thumb_up_neutral);
//        Drawable downColoredIcon = getResources().getDrawable(R.drawable.ic_thumb_down_colored);
//        cardRateUpButton.invalidateDrawable(upNeutralIcon);
//        cardRateDownButton.invalidateDrawable(downColoredIcon);

        Drawable upColoredIcon = getResources().getDrawable(R.drawable.ic_thumb_up_colored);
        Drawable downNeutralIcon = getResources().getDrawable(R.drawable.ic_thumb_down_neutral);

        cardRateUpButton.invalidate();
        cardRateDownButton.invalidate();

        cardRateUpButton.setImageDrawable(upColoredIcon);
        cardRateDownButton.setImageDrawable(downNeutralIcon);
    }

    private void colorizeCardRatingAsDown() {
        Drawable upNeutralIcon = getResources().getDrawable(R.drawable.ic_thumb_up_neutral);
        Drawable downColoredIcon = getResources().getDrawable(R.drawable.ic_thumb_down_colored);
        cardRateUpButton.setImageDrawable(upNeutralIcon);
        cardRateDownButton.setImageDrawable(downColoredIcon);
    }

    private void showAuthor() {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, currentCard.getUserId());
        startActivity(intent);
    }

    private void processEditionResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {

            case RESULT_OK:
                if (null != data) {
                    Card card = data.getParcelableExtra(Constants.CARD);
                    displayCard(card);
                } else {
                    showErrorMsg(R.string.error_displaying_card);
                    Log.e(TAG, "Intent data in activity result == null.");
                }
                break;

            case RESULT_CANCELED:
                showToast(R.string.CARD_SHOW_edition_is_cancelled);
                break;

            default:
                showErrorMsg(R.string.error_saving_card);
                break;
        }
    }

    private void processLoginForComment(int resultCode, @Nullable Intent data) {

        switch (resultCode) {

            case RESULT_OK:
                showCommentForm();
                break;

            case RESULT_CANCELED:
                showToast(R.string.login_canceled);
                break;

            default:
                showErrorMsg(R.string.login_error);
                break;
        }

    }
}
