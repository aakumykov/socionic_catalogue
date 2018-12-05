package ru.aakumykov.me.mvp.card.edit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Config;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.CardEdit_Presenter;
import ru.aakumykov.me.mvp.card.iCardEdit;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MVPUtils.FileInfo;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MVPUtils.iMVPUtils;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

@RuntimePermissions
public class CardEdit_View extends BaseView implements
    iCardEdit.View,
    TagView.OnTagClickListener
{
    @BindView(R.id.titleView) EditText titleView;

    @BindView(R.id.modeSwitcher) LinearLayout modeSwitcher;
    @BindView(R.id.modeLabel) TextView modeLabel;
    @BindView(R.id.textModeSwitch) ImageView textModeSwitch;
    @BindView(R.id.imageModeSwitch) ImageView imageModeSwitch;
    @BindView(R.id.audioModeSwitch) ImageView audioModeSwitch;
    @BindView(R.id.videoModeSwitch) ImageView videoModeSwitch;

    @BindView(R.id.mediaHolder) LinearLayout mediaHolder;

    @BindView(R.id.quoteView) EditText quoteView;

    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.addVideoButton) Button addVideoButton;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;
    @BindView(R.id.videoCodeView) TextView videoCodeView;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @BindView(R.id.descriptionView) EditText descriptionView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) EditText newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private final static String TAG = "CardEdit_View";
    private iCardEdit.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();

        CardEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        tagsContainer.setOnTagClickListener(this);

        presenter = new CardEdit_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            presenter.chooseStartVariant(getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        if (!auth().isUserLoggedIn()) closePage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                cancel();
                break;

            case R.id.actionSave:
                save();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.linkView(this);

        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                case Constants.CODE_SELECT_IMAGE:
                    try {
                        presenter.processIncomingImage(data);
                    } catch (Exception e) {
                        showErrorMsg(R.string.CARD_EDIT_error_processing_image, e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        }
    }


    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {

        if (null == card) {
            showErrorMsg(R.string.CARD_EDIT_error_displaying_card);
            Log.e(TAG, "Card is null");
            return;
        }

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                displayTextCard(card);
                break;
            case Constants.IMAGE_CARD:
                displayImageCard(card);
                break;
            case Constants.VIDEO_CARD:
                displayVideoCard(card);
                break;
            default:
                showErrorMsg(R.string.CARD_EDIT_error_editing_card);
                Log.e(TAG, "Unknown card type '"+card.getType()+"'");
        }
    }

    @Override
    public void showModeSwitcher() {
        MyUtils.show(modeLabel);
        MyUtils.show(modeSwitcher);
    }

    @Override
    public void hideModeSwitcher() {
        MyUtils.hide(modeLabel);
        MyUtils.hide(modeSwitcher);
    }

    @Override
    public void displayTitle(String text) {
        titleView.setText(text);
    }

    @Override
    public void displayQuote(String text) {
        switchTextMode();

        quoteView.setText(text);
    }

    @Override
    public void displayImage(String imageURI, boolean unprocessedYet) {

        hideModeSwitcher();

        showImageProgressBar();

        try {
            Uri uri = Uri.parse(imageURI);

            MVPUtils.loadImageWithResizeInto(
                    uri,
                    imageView,
                    unprocessedYet,
                    Config.MAX_CARD_IMAGE_WIDTH,
                    Config.MAX_CARD_IMAGE_HEIGHT,
                    new iMVPUtils.ImageLoadWithResizeCallbacks() {
                        @Override
                        public void onImageLoadWithResizeSuccess(FileInfo fileInfo) {
                            hideImageProgressBar();

                            MyUtils.hide(imagePlaceholder);

                            MyUtils.show(mediaHolder);
                            MyUtils.show(imageHolder);
                            MyUtils.show(imageView);
                            MyUtils.show(discardImageButton);
                        }

                        @Override
                        public void onImageLoadWithResizeFail(String errorMsg) {
                            hideImageProgressBar();
                            showBrokenImage();
                        }
                    }
            );

        }catch (Exception e) {
            hideImageProgressBar();
            showBrokenImage();
            e.printStackTrace();
        }
    }

    @Override
    public void displayVideo(final String videoCode) {
        prepareVideoMode();

        MyUtils.hide(addVideoButton);
        MyUtils.show(removeVideoButton);

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
                        youTubePlayer.cueVideo(videoCode, 0.0f);
                        MyUtils.show(youTubePlayerView);
                    }
                });
            }
        }, true);
    }

    @Override
    public void showBrokenImage() {
        MyUtils.hide(imageView);
        imagePlaceholder.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.show(imagePlaceholder);
    }

    @Override
    public String getCardTitle() {
        return titleView.getText().toString();
    }

    @Override
    public String getCardQuote() {
        return quoteView.getText().toString();
    }

    @Override
    public String getCardDescription() {
        return descriptionView.getText().toString();
    }

    @Override
    public HashMap<String,Boolean> getCardTags() {
        HashMap<String,Boolean> map = new HashMap<>();
        List<String> tagsList = tagsContainer.getTags();
        for (String tagName : tagsList) {
            map.put(tagName, true);
        }
        return map;
    }

    @Override
    public byte[] getImageData() throws Exception {
        return MVPUtils.imageView2Bitmap(imageView);
    }

    @Override
    public void storeCardVideoCode(String videoCode) {
        videoCodeView.setText(videoCode);
    }

    @Override
    public String getCardVideoCode() {
        return videoCodeView.getText().toString();
    }

    @Override
    public void showImageProgressBar() {
        MyUtils.show(imageProgressBar);
    }

    @Override
    public void hideImageProgressBar() {
        MyUtils.hide(imageProgressBar);
    }

    @Override
    public void setImageUploadProgress(int progress) {
//        imageProgressBar.setProgress(progress);
        Log.d(TAG, "imageUploadProgress: "+progress);
    }

    @Override
    public void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);

        newTagInput.setEnabled(false);
        addTagButton.setEnabled(false);

        saveButton.setEnabled(false);

        MyUtils.show(imageProgressBar);
    }

    @Override
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);

        newTagInput.setEnabled(false);
        addTagButton.setEnabled(false);

        saveButton.setEnabled(true);

        MyUtils.hide(imageProgressBar);
    }





    @Override
    public void goCardShow(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void finishEdit(Card card) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, card);
        setResult(RESULT_OK, intent);
        finish();
    }

    // TODO: перенести в Utils
    @Override
    public String detectMimeType(Uri dataURI) {
        ContentResolver cr = this.getContentResolver();
        return cr.getType(dataURI);
    }


    // Методы нажатий
    @OnClick(R.id.textModeSwitch)
    void switchTextMode() {
        hideModeSwitcher();

        MyUtils.show(mediaHolder);
        MyUtils.show(quoteView);

        quoteView.requestFocus();

        presenter.setCardType(Constants.TEXT_CARD);
    }

    @OnClick(R.id.imageModeSwitch)
    void switchImageMode() {
        hideModeSwitcher();

        MyUtils.show(mediaHolder);
        MyUtils.show(imageHolder);
        MyUtils.show(imagePlaceholder);

        presenter.setCardType(Constants.IMAGE_CARD);
    }

    @OnClick(R.id.audioModeSwitch)
    void switchAudioMode() {

    }

    @OnClick(R.id.videoModeSwitch)
    void switchVideoMode() {
        prepareVideoMode();
        addVideo();
    }

    @OnClick(R.id.addVideoButton)
    void addVideo() {
        MyDialogs.addYoutubeVideoDialog(this, new iMyDialogs.StringInputCallback() {
            @Override
            public void onDialogWithStringYes(String text) {
                storeCardVideoCode(text);
                displayVideo(text);
            }
        });
    }

    @OnClick(R.id.removeVideoButton)
    void removeVideo() {
        // youTubePlayer может ещё быть не инициализированным
        if (null != youTubePlayer) youTubePlayer.pause();

        youTubePlayerView.release();

        videoPlayerHolder.removeAllViews();

        videoCodeView.setText("");

        MyUtils.show(addVideoButton);
        MyUtils.hide(removeVideoButton);
    }

    @OnClick(R.id.saveButton)
    void save() {
        // TODO: показывать причину ошибки сохранения
        try {
            presenter.saveCard();
        } catch (Exception e) {
            enableForm();
            hideProgressBar();
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancelButton)
    void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.discardImageButton)
    void removeImage() {
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);

        imagePlaceholder.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
        MyUtils.show(imagePlaceholder);

//        imageView.setTag(Constants.)
    }

    @OnClick(R.id.imagePlaceholder)
    public void selectImage() {
        Log.d(TAG, "selectImage()");

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
            showErrorMsg(R.string.CARD_EDIT_error_receiving_image);
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.addTagButton)
    public void addTag() {
        String newTag = newTagInput.getText().toString();
        newTag = presenter.processNewTag(newTag);

        // TODO: отображать ошибку или молча исправлять её?

        /* Не добавлять пустую и дублирующую метку - очевидная логика,
         * поэтому обрабатывается здесь, а не в презентере. */
        if (null != newTag) {
            if (!getCardTags().containsKey(newTag)) {
                tagsContainer.addTag(newTag);
            }
        }

        newTagInput.setText("");
        newTagInput.requestFocus();
    }

    // меток
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
    private void prepareVideoMode() {
        hideModeSwitcher();
        MyUtils.show(mediaHolder);
        MyUtils.show(addVideoButton);
        presenter.setCardType(Constants.VIDEO_CARD);
    }

    private void displayTextCard(Card card) {
        switchTextMode();
        displayCommonCardParts(card);
        quoteView.setText(card.getQuote());
    }

    private void displayImageCard(Card card) {
        switchImageMode();
        displayCommonCardParts(card);
        displayImage(card.getImageURL(), false);
    }

    private void displayVideoCard(Card card) {
        displayCommonCardParts(card);
        displayVideo(card.getVideoCode());
        storeCardVideoCode(card.getVideoCode());
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        showTags(card.getTags());
    }

    private void showTags(HashMap<String,Boolean> tagsMap) {
        Log.d(TAG, "displayTags(), "+tagsMap);
        if (null != tagsMap) {
            List<String> tagsList = new ArrayList<>(tagsMap.keySet());
            tagsContainer.setTags(tagsList);
            tagsContainer.setEnableCross(true);
        }
    }

    // Другие
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }
}
