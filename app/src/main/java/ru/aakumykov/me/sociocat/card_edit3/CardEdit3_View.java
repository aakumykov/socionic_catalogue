package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MVPUtils.FileInfo;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.iMVPUtils;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.YesNoDialog;

public class CardEdit3_View extends BaseView implements
        iCardEdit3.View,
        TagView.OnTagClickListener
{
    @BindView(R.id.titleInput) EditText titleInput;
    @BindView(R.id.quoteInput) EditText quoteInput;
    @BindView(R.id.quoteSourceInput) EditText quoteSourceInput;
    @BindView(R.id.descriptionInput) EditText descriptionInput;

    @BindView(R.id.mediaThrobber) ProgressBar mediaThrobber;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;

    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;
    @BindView(R.id.addVideoButton) Button addVideoButton;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) EditText newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    @BindView(R.id.imageURL_hiddenField) TextView imageURL_hiddenField;
    @BindView(R.id.videoCode_hiddenField) TextView videoCode_hiddenField;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private iCardEdit3.Presenter presenter;
    private boolean firstRun = true;
    private boolean cancelledByUser = false;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit3_activity);
        ButterKnife.bind(this);

        activateUpButton();

        presenter = new CardEdit3_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            try {
                presenter.processInputIntent(getIntent());
            } catch (Exception e) {
                showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!cancelledByUser) {
            presenter.saveEditState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //presenter.restoreEditState();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (cancelledByUser) presenter.clearEditState();
//        else presenter.saveEditState();

        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed(); // Если не закомментировать, выходит, игнорируя диалог
        cancelEdit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.linkView(this); // обязательно!!!

        switch (requestCode) {
            case Constants.CODE_SELECT_IMAGE:
                try {
                    presenter.processSelectedImage(resultCode, data);
                } catch (Exception e) {
                    showErrorMsg(R.string.CARD_EDIT_error_processing_image, e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {
        hideProgressBar();

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                displayQuote(card.getQuote(), card.getQuoteSource());
                break;
            case Constants.IMAGE_CARD:
                displayImage(card.getImageURL());
                break;
            case Constants.VIDEO_CARD:
                displayVideo(card.getVideoCode());
                break;
            default:
                showErrorMsg(R.string.wrong_card_type);
        }

        displayCommonCardParts(card);
    }

    @Override
    public void displayImage(String imageURI, boolean unprocessedYet) {

        MyUtils.hide(imagePlaceholder);
        MyUtils.hide(imageHolder);
        MyUtils.show(mediaThrobber);

        Picasso.get().load(imageURI)
                .into(imageView, new Callback() {
                    @Override public void onSuccess() {
                        MyUtils.hide(mediaThrobber);
                        MyUtils.hide(imagePlaceholder);
                        MyUtils.show(imageHolder);
                        MyUtils.show(imageView);
                        MyUtils.show(discardImageButton);
                    }

                    @Override public void onError(Exception e) {
                        showBrokenImage();
                    }
                });

//        try {
//            Uri uri = Uri.parse(imageURI);
//
//            MVPUtils.loadImageWithResizeInto(
//                    uri,
//                    imageView,
//                    unprocessedYet,
//                    Config.MAX_CARD_IMAGE_WIDTH,
//                    Config.MAX_CARD_IMAGE_HEIGHT,
//                    new iMVPUtils.ImageLoadWithResizeCallbacks() {
//                        @Override
//                        public void onImageLoadWithResizeSuccess(FileInfo fileInfo) {
//                            MyUtils.hide(mediaThrobber);
//                            MyUtils.hide(imagePlaceholder);
//                            MyUtils.show(imageHolder);
//                            MyUtils.show(imageView);
//                            MyUtils.show(discardImageButton);
//                        }
//
//                        @Override
//                        public void onImageLoadWithResizeFail(String errorMsg) {
//                            showBrokenImage();
//                        }
//                    }
//            );
//
//        }catch (Exception e) {
//            showBrokenImage();
//        }
    }

    @Override
    public String getCardTitle() {
        return titleInput.getText().toString();
    }

    @Override
    public String getQuote() {
        return quoteInput.getText().toString();
    }

    @Override
    public String getQuoteSource() {
        return quoteSourceInput.getText().toString();
    }

    @Override
    public String getImageURL() {
        return imageURL_hiddenField.getText().toString();
    }

    @Override
    public String getVideoCode() {
        return videoCode_hiddenField.getText().toString();
    }

    @Override
    public String getDescription() {
        return descriptionInput.getText().toString();
    }


    // Методы событий интерсейса
    @OnClick(R.id.imagePlaceholder)
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            showErrorMsg(R.string.CARD_EDIT_error_receiving_image,
                    "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.discardImageButton)
    void resetImage() {
        imageView.setImageDrawable(null);
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);
        MyUtils.show(imagePlaceholder);
    }

    @OnClick(R.id.addTagButton)
    void addTag() {
        String tag = MVPUtils.normalizeTag(newTagInput.getText().toString());
        tagsContainer.addTag(tag);
    }

    @OnClick(R.id.saveButton)
    void saveCard() {

    }

    @OnClick(R.id.cancelButton)
    void cancelEdit() {
        MyDialogs.cancelEditDialog(
                this,
                R.string.CARD_EDIT_cancel_editing_title,
                R.string.CARD_EDIT_cancel_editing_message,
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
                    }

                    @Override
                    public void onYesInDialog() {
                        clearSharedPrefsData(getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT), Constants.CARD);
                        cancelledByUser = true;
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
        );
    }


    // Методы обратнаго вызова
    @Override
    public void onTagClick(int position, String text) {

    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {
        tagsContainer.removeTag(position);
    }


    // Внутренние методы
    private void displayQuote(String... quoteParts) {
        quoteInput.setText(quoteParts[0]);
        if (2 == quoteParts.length)
            quoteSourceInput.setText(quoteParts[1]);

        MyUtils.show(quoteInput);
        MyUtils.show(quoteSourceInput);
    }

    private void displayImage(final String imageURL) {

        MyUtils.show(imageHolder);

        if (TextUtils.isEmpty(imageURL)) {
            MyUtils.show(imagePlaceholder);
        }
        else {
            MyUtils.hide(imagePlaceholder);
            MyUtils.show(mediaThrobber);

            Picasso.get().load(imageURL).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    MyUtils.hide(mediaThrobber);
                    MyUtils.show(imageView);
                    imageURL_hiddenField.setText(imageURL);
                }

                @Override
                public void onError(Exception e) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_image_broken);
                    imageView.setImageDrawable(drawable);
                }
            });
        }
    }

    private void displayVideo(final String videoCode) {

        MyUtils.hide(addVideoButton);
        MyUtils.show(mediaThrobber);

        int playerWidth = MyUtils.getScreenWidth(this);
        int playerHeight = Math.round(MyUtils.getScreenWidth(this) * 9f/16f);

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
                        youTubePlayer.cueVideo(videoCode, 0.0f);

                        videoCode_hiddenField.setText(videoCode);

                        MyUtils.hide(mediaThrobber);
                        MyUtils.show(videoPlayerHolder);
                        MyUtils.show(youTubePlayerView);
                        MyUtils.show(removeVideoButton);
                    }
                });
            }
        }, true);
    }

    private void displayCommonCardParts(Card card) {
        titleInput.setText(card.getTitle());
        descriptionInput.setText(card.getDescription());
        tagsContainer.setTags(new ArrayList<String>(card.getTags().keySet()));
    }

    private void showBrokenImage() {
        MyUtils.hide(mediaThrobber);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_image_broken);
        imageView.setImageDrawable(drawable);
    }
}
