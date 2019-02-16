package ru.aakumykov.me.sociocat.card_edit3;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardEdit3_View extends BaseView implements iCardEdit3.View
{
    @BindView(R.id.titleInput) EditText titleInput;
    @BindView(R.id.quoteInput) EditText quoteInput;
    @BindView(R.id.quoteSourceInput) EditText quoteSourceInput;
    @BindView(R.id.descriptionInput) EditText descriptionInput;

    @BindView(R.id.mediaThrobber) ProgressBar mediaThrobber;

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;

    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;
    @BindView(R.id.addVideoButton) Button addVideoButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private iCardEdit3.Presenter presenter;
    private boolean firstRun = true;


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
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {
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


    // Методы событий интерсейса
    @OnClick(R.id.saveButton)
    void saveCard() {

    }

    @OnClick(R.id.cancelButton)
    void cancelEdit() {

    }


    // Внутренние методы
    private void displayQuote(String... quoteParts) {
        quoteInput.setText(quoteParts[0]);
        if (2 == quoteParts.length)
            quoteSourceInput.setText(quoteParts[1]);

        MyUtils.show(quoteInput);
        MyUtils.show(quoteSourceInput);
    }

    private void displayImage(String imageURL) {

        MyUtils.hide(imagePlaceholder);
        MyUtils.show(mediaThrobber);

        Picasso.get().load(imageURL).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                MyUtils.hide(mediaThrobber);
                MyUtils.show(imageView);
            }

            @Override
            public void onError(Exception e) {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_image_broken);
                imageView.setImageDrawable(drawable);
            }
        });

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
    }
}
