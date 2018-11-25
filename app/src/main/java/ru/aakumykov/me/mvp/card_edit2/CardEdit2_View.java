package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardEdit2_View extends BaseView implements iCardEdit2.View {

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
    @BindView(R.id.videoPlayerThrobber) ProgressBar videoPlayerThrobber;
    @BindView(R.id.youTubePlayerView) YouTubePlayerView youTubePlayerView;
    @BindView(R.id.addVideoButton) Button addVideoButton;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;

    // Разметка показа меток
    @BindView(R.id.tagsViewHolder) LinearLayout tagsViewHolder;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    // Разметка ввода меток
    @BindView(R.id.tagsInputHolder) LinearLayout tagsInputHolder;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iCardEdit2.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit2_activity);
        ButterKnife.bind(this);

        activateUpButton();

        setPageTitle(R.string.CARD_EDIT_page_title);

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

        if (null == card) quoteInput.requestFocus();
        else fillTextCardForm(card);
    }

    @Override
    public void switchImageMode(@Nullable Card card) {
        MyUtils.hide(modeSwitcher);
        MyUtils.show(imageHolder);

        if (null == card) {
            MyUtils.show(imagePlaceholder);
        }
        else fillImageCardForm(card);
    }

    @Override
    public void switchVideoMode(@Nullable Card card) {
        MyUtils.hide(modeSwitcher);
        MyUtils.show(videoHolder);

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


    // Внутренние методы
    private void fillTextCardForm(final Card card) {
        quoteInput.setText(card.getQuote());
        fillCommonCardParts(card);
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

        MyUtils.show(videoPlayerThrobber);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {

            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {

                    @Override
                    public void onReady() {
                        String videoCode = card.getVideoCode();
                        initializedYouTubePlayer.cueVideo(videoCode, 0f);
                        MyUtils.hide(videoPlayerThrobber);
                        MyUtils.show(youTubePlayerView);
                    }

                });
            }
        }, true);
    }

    private void fillCommonCardParts(final Card card) {
        titleInput.setText(card.getTitle());
        descriptionInput.setText(card.getDescription());
        List<String> tags = new ArrayList<>(card.getTags().keySet());
        tagsContainer.setTags(tags);
    }
}
