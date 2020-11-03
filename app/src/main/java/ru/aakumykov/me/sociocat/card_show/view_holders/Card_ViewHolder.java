package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.AnimationUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Card_ViewHolder extends Base_ViewHolder implements
        iCard_ViewHolder,
        TagView.OnTagClickListener
{
//    @BindView(R.id.cardLayout) LinearLayout cardLayout;

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.videoThrobber) ImageView videoThrobber;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;
    @BindView(R.id.youTubePlayerView) YouTubePlayerView youTubePlayerView;
    @BindView(R.id.quoteSourceView) TextView quoteSourceView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.authorView) TextView authorView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    @BindView(R.id.cardRateUpWidget) ImageView cardRatingUpWidget;
    @BindView(R.id.cardRateDownWidget) ImageView cardRatingDownWidget;
    @BindView(R.id.cardRatingView) TextView cardRatingView;
    @BindView(R.id.cardRatingThrobber) ProgressBar cardRatingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;

    private enum MediaType { AUDIO, VIDEO }

    private static final String TAG = "Card_ViewHolder";
    private iList_Item currentListItem = null;
    private Card currentCard = null;
    private iCardShow.iPresenter presenter = null;


    public Card_ViewHolder(@NonNull View itemView, iCardShow.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }


    // Base_ViewHolder
    @Override
    public void initialize(iList_Item listItem) {
        this.currentListItem = listItem;
        this.currentCard = (Card) listItem.getPayload();

        tagsContainer.setOnTagClickListener(this);

        displayCard();
    }


    // iCardViewHolder
    @Override
    public void disableRatingControls() {
        MyUtils.disable(cardRatingUpWidget);
        MyUtils.disable(cardRatingDownWidget);

        MyUtils.hide(cardRatingView);
        MyUtils.show(cardRatingThrobber);
    }

    @Override
    public void setRating(int value) {
        cardRatingView.setText(String.valueOf(value));
        MyUtils.show(cardRatingView);
        MyUtils.hide(cardRatingThrobber);
    }

    @Override
    public void enableRatingControls() {
        MyUtils.enable(cardRatingUpWidget);
        MyUtils.enable(cardRatingDownWidget);

        MyUtils.show(cardRatingView);
        MyUtils.hide(cardRatingThrobber);
    }


    @Override
    public void setCardRatedUp() {
        cardRatingUpWidget.setImageResource(R.drawable.ic_thumb_up_colored);
        cardRatingDownWidget.setImageResource(R.drawable.ic_thumb_down_neutral);
    }

    @Override
    public void setCardRatedDown() {
        cardRatingUpWidget.setImageResource(R.drawable.ic_thumb_up_neutral);
        cardRatingDownWidget.setImageResource(R.drawable.ic_thumb_down_colored);
    }

    @Override
    public void setCardNotRated() {
        cardRatingUpWidget.setImageResource(R.drawable.ic_thumb_up_neutral);
        cardRatingDownWidget.setImageResource(R.drawable.ic_thumb_down_neutral);
    }


    // Нажатия
    @OnClick(R.id.replyWidget)
    void onReplyClicked() {
        presenter.onReplyClicked(currentListItem);
    }

    @OnClick(R.id.cardRateUpWidget)
    void onRateUpClicked() {
        presenter.onCardRateUpClicked(this);
    }

    @OnClick(R.id.cardRateDownWidget)
    void onRateDownClicked() {
        presenter.onCardRateDownClicked(this);
    }

    @OnClick(R.id.authorView)
    void onAuthorClicked() {
        presenter.onAuthorClicked();
    }


    // TagView.OnTagClickListener
    @Override
    public void onTagClick(int position, String text) {
        presenter.onTagClicked(text);
    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onSelectedTagDrag(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {

    }


    // Внутренние методы
    private void displayCard() {

        titleView.setText(currentCard.getTitle());
        descriptionView.setText(currentCard.getDescription());

        displayQuoteSource();

        switch (currentCard.getType()) {
            case Card.TEXT_CARD:
                quoteView.setText(currentCard.getQuote());
                MyUtils.show(quoteView);
                break;

            case Card.IMAGE_CARD:
                displayImage();
                break;

            case Card.VIDEO_CARD:
                displayMedia(MediaType.VIDEO);
                break;

            case Card.AUDIO_CARD:
                displayMedia(MediaType.AUDIO);
                break;

            default:
                break;
        }

        displayAuthor();

        displayTags();

        cardRatingView.setText(String.valueOf(currentCard.getRating()));

        presenter.onCardAlmostDisplayed(this);
    }

    private void displayQuoteSource() {
        String quoteSource = currentCard.getQuoteSource();
        if (!TextUtils.isEmpty(quoteSource)) {
            quoteSourceView.setText(MyUtils.getStringWithString(quoteSourceView.getContext(), R.string.CARD_SHOW_quote_source, quoteSource));
            MyUtils.show(quoteSourceView);
        }
    }

    private void displayMedia(MediaType mediaType) {

        switch (mediaType) {
            case AUDIO:
                InsertableYoutubePlayer insertableYoutubePlayer =
                new InsertableYoutubePlayer(videoContainer.getContext(), videoContainer);

                insertableYoutubePlayer.show(
                        currentCard.getAudioCode(),
                        currentCard.getTimecode(),
                        InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER,
                        R.string.YOUTUBE_PLAYER_waiting_for_audio
                );
                break;

            case VIDEO:
                /*insertableYoutubePlayer.show(
                        currentCard.getVideoCode(),
                        currentCard.getTimecode(),
                        InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER,
                        R.string.YOUTUBE_PLAYER_waiting_for_video
                );*/
                displayVideo();
                break;

            default:
                throw new RuntimeException("Unknown mediaType: "+mediaType);
        }
    }

    private void displayAuthor() {
        authorView.setText(
                MyUtils.getStringWithString(
                        authorView.getContext(),
                        R.string.CARD_SHOW_author,
                        currentCard.getUserName()
                )
        );
    }

    private void displayTags() {
        tagsContainer.removeAllTags();

        List<String> tagsList = currentCard.getTags();
        if (tagsList.size() > 0) {
            for (String tag : tagsList)
                tagsContainer.addTag(tag);
            MyUtils.show(tagsContainer);
        }
    }

    private void displayImage() {

        /* С ImageLoader-ом опять пошли артефакты
        imageView.setImageResource(R.drawable.ic_image_placeholder_monochrome);

        ImageLoader.loadImage(imageView.getContext(), currentCard.getImageURL(), new ImageLoader.LoadImageCallbacks() {
            @Override
            public void onImageLoadSuccess(Bitmap imageBitmap) {
                CardUtils.smartDisplayImage(imageView, imageBitmap);
            }

            @Override
            public void onImageLoadError(String errorMsg) {
                imageView.setImageResource(R.drawable.ic_image_error);
            }
        });*/

        /*Glide.with(imageView.getContext())
                .load(currentCard.getImageURL())
                .placeholder(R.drawable.ic_image_placeholder_smaller)
                .error(R.drawable.ic_image_error)
                .into(imageView);*/

        imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        MyUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        Glide.with(imageView.getContext())
                .load(currentCard.getImageURL())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        imageView.setImageResource(R.drawable.ic_image_error);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setImageDrawable(resource);
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        return false;
                    }
                })
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                    }
                });
    }

    private void displayVideo() {

        MyUtils.show(videoThrobber);
        videoThrobber.startAnimation(AnimationUtils.createFadeInOutAnimation(500L, false));

        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                MyUtils.hide(videoThrobber);
                videoThrobber.clearAnimation();

                youTubePlayer.cueVideo(currentCard.getVideoCode(), currentCard.getTimecode());
                MyUtils.show(youTubePlayerView);
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        });
    }

    private void showImageError(String errorMsg) {
        imageView.setImageResource(R.drawable.ic_image_error);
        MyUtils.showCustomToast(imageView.getContext(), errorMsg);
    }
}
