package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardEdit2_View extends BaseView implements
        iCardEdit2.View,
        TagView.OnTagClickListener
{

    @BindView(R.id.titleInput) EditText titleInput;
    @BindView(R.id.quoteInput) EditText quoteInput;
    @BindView(R.id.descriptionInput) EditText descriptionInput;

    @BindView(R.id.modeSwitcher) LinearLayout modeSwitcher;

    // Разметка для картинки
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;

    // Разметка для видео
    @BindView(R.id.videoHolder) LinearLayout videoHolder;
    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.addVideoButton) Button addVideoButton;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;

    // Разметка показа меток
    @BindView(R.id.tagsViewHolder) LinearLayout tagsViewHolder;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    // Разметка ввода меток
    @BindView(R.id.tagsInputHolder) LinearLayout tagsInputHolder;
    @BindView(R.id.newTagInput) EditText newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    // Используется для хранения кода видео
    @BindView(R.id.videoCodeView) TextView videoCodeView;


    private boolean firstRun = true;
    private iCardEdit2.Presenter presenter;

    // TODO: переделать на ButterKnife
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit2_activity);
        ButterKnife.bind(this);

        activateUpButton();

        setPageTitle(R.string.CARD_EDIT_page_title);

        tagsContainer.setOnTagClickListener(this);
        tagsContainer.setIsTagViewClickable(true);

        presenter = new CardEdit2_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            presenter.processInputIntent(getIntent());
            firstRun = false;
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
        showToast("CardEdit2_View.destroy()");
        if (null != youTubePlayerView)
            youTubePlayerView.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                saveCard();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
    }


    // Интерфейсные методы
    @Override
    public void showModeSwitcher() {
        MyUtils.show(modeSwitcher);
    }

    @Override
    public void hideModeSwitcher() {
        MyUtils.hide(modeSwitcher);
    }

    @Override
    public void switchTextMode(@Nullable Card card) {
        MyUtils.hide(modeSwitcher);
        MyUtils.show(quoteInput);

        presenter.setCardType(Constants.TEXT_CARD);

        if (null == card) quoteInput.requestFocus();
        else fillTextCardForm(card);
    }

    @Override
    public void switchImageMode(@Nullable Card card) {
        MyUtils.hide(modeSwitcher);
        MyUtils.show(imageHolder);

        presenter.setCardType(Constants.IMAGE_CARD);

        if (null == card) {
            MyUtils.show(imagePlaceholder);
        }
        else fillImageCardForm(card);
    }

    @Override
    public void switchVideoMode(@Nullable Card card) {
        MyUtils.hide(modeSwitcher);
        MyUtils.show(videoHolder);

        presenter.setCardType(Constants.VIDEO_CARD);

        if (null == card) {
            MyUtils.show(addVideoButton);
        } else {
            fillVideoCardForm(card);
        }
    }

    @Override
    public void switchAudioMode(@Nullable Card card) {
        String msg = getResources().getString(R.string.not_yet_implamented);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

//        presenter.setCardType(Constants.AUDIO_CARD);
    }

    @Override
    public String getCardTitle() {
        return titleInput.getText().toString();
    }

    @Override
    public String getCardQuote() {
        return quoteInput.getText().toString();
    }

    @Override
    public String getVideoCode() {
        return videoCodeView.getText().toString();
    }

    @Override
    public String getCardDescription() {
        return descriptionInput.getText().toString();
    }

    @Override
    public HashMap<String, Boolean> getCardTags() {
        HashMap<String,Boolean> tagsMap = new HashMap<>();
        for(String tagName : tagsContainer.getTags()) {
            tagsMap.put(tagName, true);
        }
        return tagsMap;
    }

    @Override
    public void setCardTitle(String title) {
        titleInput.setText(title);
    }

    @Override
    public void setCardQuote(String quote) {
        quoteInput.setText(quote);
    }

    @Override
    public void disableForm() {
        MyUtils.disable(titleInput);
        MyUtils.disable(quoteInput);

        MyUtils.disable(descriptionInput);
        MyUtils.disable(discardImageButton);
        MyUtils.disable(addVideoButton);
        MyUtils.disable(removeVideoButton);

        MyUtils.disable(newTagInput);
        MyUtils.disable(addTagButton);

        MyUtils.disable(saveButton);
//        MyUtils.disable();
    }

    @Override
    public void enableForm() {
        MyUtils.enable(titleInput);
        MyUtils.enable(quoteInput);

        MyUtils.enable(descriptionInput);
        MyUtils.enable(discardImageButton);
        MyUtils.enable(addVideoButton);
        MyUtils.enable(removeVideoButton);

        MyUtils.enable(newTagInput);
        MyUtils.enable(addTagButton);

        MyUtils.enable(saveButton);
//        MyUtils.disable();
    }

    @Override
    public void addTag(String tagName) {
        tagsContainer.addTag(tagName);
        newTagInput.setText("");
    }

    @Override
    public void finishEdit(Card updatedCard) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, updatedCard);
        setResult(RESULT_OK, intent);
        finish();
    }


    // Методы обратного вызова
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


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    // Нажатия
    @OnClick({R.id.textModeSwitch, R.id.imageModeSwitch, R.id.audioModeSwitch, R.id.videoModeSwitch})
    void switchMode(View view) {
        switch (view.getId()) {
            case R.id.textModeSwitch:
                switchTextMode(null);
                break;
            case R.id.imageModeSwitch:
                switchImageMode(null);
                break;
            case R.id.videoModeSwitch:
                switchVideoMode(null);
                break;
            case R.id.audioModeSwitch:
                switchAudioMode(null);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.imagePlaceholder)
    void selectImage() {

    }

    @OnClick(R.id.removeVideoButton)
    void removeVideo() {
        youTubePlayerView.release();
        videoPlayerHolder.removeAllViews();
        clearVideoCode();
        MyUtils.hide(removeVideoButton);
        MyUtils.show(addVideoButton);
    }

    @OnClick(R.id.addVideoButton)
    void addVideo() {
        // TODO: проверка на пустоту, да и на формат...
        MyDialogs.addYoutubeVideoDialog(
                this,
                new iMyDialogs.StringInputCallback() {
                    @Override
                    public void onDialogWithStringYes(String text) {
                        storeVideoCode( MVPUtils.extractYoutubeVideoCode(text) );
                        displayVideo(getVideoCode());

                        MyUtils.hide(addVideoButton);
                        // TODO: это нужно делать после появления видео
                        MyUtils.show(removeVideoButton);
                    }
                }
        );
    }

    @OnClick(R.id.saveButton)
    void saveCard() {
        // Нужно ли здесь перехватывать исключения?
        try {
            presenter.saveCard();
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancelButton)
    void cancelEdit() {
        setResult(RESULT_CANCELED);
        closePage();
    }

    @OnClick(R.id.addTagButton)
    void addTagClicked() {
        presenter.processTag(newTagInput.getText().toString());
    }


    // Внутренние методы
    private void fillTextCardForm(final Card card) {
        fillCommonCardParts(card);
        quoteInput.setText(card.getQuote());
    }

    private void fillImageCardForm(final Card card) {
        fillCommonCardParts(card);

        MyUtils.hide(imagePlaceholder);
        MyUtils.show(imageProgressBar);

        Picasso.get()
                .load(card.getImageURL())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        MyUtils.hide(imageProgressBar);
                        MyUtils.show(imageView);
                    }

                    @Override
                    public void onError(Exception e) {
                        imagePlaceholder.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_image_broken)
                        );
                    }
                });
    }

    private void fillVideoCardForm(final Card card) {
        fillCommonCardParts(card);
        displayVideo(card.getVideoCode());
    }

    private void fillCommonCardParts(final Card card) {
        titleInput.setText(card.getTitle());
        descriptionInput.setText(card.getDescription());

        List<String> tags = new ArrayList<>(card.getTags().keySet());
        tagsContainer.setTags(tags);
    }

    private void displayVideo(final String videoCode) {
        int playerWidth = MyUtils.getScreenWidth(this);
        int playerHeight = Math.round(MyUtils.getScreenWidth(this) * 9/16);

        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setMinimumWidth(playerWidth);
        youTubePlayerView.setMinimumHeight(playerHeight);

        videoPlayerHolder.addView(youTubePlayerView);
        MyUtils.show(videoPlayerHolder);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer = initializedYouTubePlayer;
                        youTubePlayer.cueVideo(videoCode, 0.0f);

//                        MyUtils.show(youTubePlayerView);
                        MyUtils.show(removeVideoButton);
                    }
                });
            }
        }, true);
    }


    // Манипуляции с кодом видео
    private void storeVideoCode(String videoCode) {
        videoCodeView.setText(videoCode);
    }

    private void clearVideoCode() {
        videoCodeView.setText("");
    }
}
