package io.gitlab.aakumykov.sociocat.card_show.view_holders;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import io.gitlab.aakumykov.insertable_yotube_player.InsertableYoutubePlayer;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.card_show.iCardShow;
import io.gitlab.aakumykov.sociocat.card_show.list_items.iList_Item;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.utils.AnimationUtils;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;

public class Card_ViewHolder extends Base_ViewHolder implements
        iCard_ViewHolder,
        TagView.OnTagClickListener
{
//    @BindView(R.id.cardLayout) LinearLayout cardLayout;

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.mediaView) ImageView imageView;
    @BindView(R.id.videoThrobber) ImageView videoThrobber;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;
    @BindView(R.id.quoteSourceView) TextView quoteSourceView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.authorView) TextView authorView;

    @BindView(R.id.canonicalTagsContainer) TagContainerLayout tagsContainer;

    @BindView(R.id.cardRateUpWidget) ImageView cardRatingUpWidget;
    @BindView(R.id.cardRateDownWidget) ImageView cardRatingDownWidget;
    @BindView(R.id.cardRatingView) TextView cardRatingView;
    @BindView(R.id.cardRatingThrobber) ProgressBar cardRatingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;

    private enum MediaType { AUDIO, VIDEO }

    private static final String TAG = "CardViewHolder";
    private iList_Item currentListItem = null;
    private Card currentCard = null;
    private iCardShow.iPresenter presenter = null;
    private YouTubePlayerView mYoutubePlayerView;

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
        presenter.onAddCommentClicked(currentListItem);
    }

    @OnClick(R.id.cardRateUpWidget)
    void onRateUpClicked() {
        presenter.onCardRateUpClicked(this);
    }

    @OnClick(R.id.cardRateDownWidget)
    void onRateDownClicked() {
        presenter.onCardRateDownClicked(this);
    }

    @OnClick(R.id.quoteSourceView)
    void onQuoteSourceClicked() {
        presenter.onQuoteSourceClicked(currentCard);
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

        displayCommonCardParts();

        switch (currentCard.getType()) {
            case Card.TEXT_CARD:
                displayTextCard();
                break;

            case Card.IMAGE_CARD:
                displayImageCard();
                break;

            case Card.VIDEO_CARD:
                displayAudioVideo(MediaType.VIDEO);
                break;

            case Card.AUDIO_CARD:
                displayAudioVideo(MediaType.AUDIO);
                break;

            default:
                break;
        }

        presenter.onCardAlmostDisplayed(this);
    }


    private void displayCommonCardParts() {
        displayTitle();

        displayDescription();

        displayQuoteSource();

        displayAuthor();

        displayDate();

        displayTags();

        displayRating();
    }

    private void displayTextCard() {
        hideVideo();
        hideAudio();
        hideImage();

        displayQuote();
    }

    private void displayImageCard() {
        hideQuote();
        hideVideo();
        hideAudio();

        displayImage();
    }

    private void displayAudioVideo(MediaType mediaType) {
        hideQuote();
        hideImage();

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

    private void displayVideo() {

        MyUtils.show(videoThrobber);
        videoThrobber.startAnimation(AnimationUtils.createFadeInOutAnimation(750L, false));

        if (null != mYoutubePlayerView)
            mYoutubePlayerView.release();

        mYoutubePlayerView = new YouTubePlayerView(videoContainer.getContext());
        mYoutubePlayerView.setEnableAutomaticInitialization(false);
        mYoutubePlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mYoutubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                videoThrobber.clearAnimation();
                MyUtils.hide(videoThrobber);

                videoContainer.addView(mYoutubePlayerView);
                MyUtils.show(videoContainer);

                youTubePlayer.cueVideo(currentCard.getVideoCode(), currentCard.getTimecode());
            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);

                videoThrobber.clearAnimation();
                videoThrobber.setImageResource(R.drawable.ic_youtube_video_error);
            }
        });
    }


    private void displayTitle() {
        titleView.setText(currentCard.getTitle());
    }

    private void displayQuote() {
        quoteView.setText(currentCard.getQuote());
        MyUtils.show(quoteView);
    }

    private void displayQuoteSource() {

        String quoteSource = currentCard.getQuoteSource();

        if (!TextUtils.isEmpty(quoteSource)) {
            String text = MyUtils.getStringWithString(quoteSourceView.getContext(), R.string.CARD_SHOW_quote_source, quoteSource);
            quoteSourceView.setText(text);
            MyUtils.show(quoteSourceView);
        }
        else
            MyUtils.hide(quoteSourceView);
    }

    private void displayDescription() {
        descriptionView.setText(currentCard.getDescription());
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

    private void displayDate() {

        long cTime = currentCard.getCTime();
        long mTime = currentCard.getMTime();

        if (mTime == cTime) {
            MyUtils.hide(mTimeView);

            String cTimeString = MyUtils.getHumanTimeAgo(cTimeView.getContext(), cTime, R.string.simple_string);
            cTimeView.setText(cTimeString);
            MyUtils.show(cTimeView);
            return;
        }

        String cTimeString = MyUtils.getHumanTimeAgo(cTimeView.getContext(), cTime, R.string.CARD_SHOW_created_at);
        String mTimeString = MyUtils.getHumanTimeAgo(mTimeView.getContext(), mTime, R.string.CARD_SHOW_edited_at);

        cTimeView.setText(cTimeString);
        mTimeView.setText(mTimeString);

        MyUtils.show(cTimeView);
        MyUtils.show(mTimeView);
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

    private void displayRating() {
        cardRatingView.setText(String.valueOf(currentCard.getRating()));
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


    private void hideQuote() {
        MyUtils.hide(quoteView);
        MyUtils.hide(quoteSourceView);
    }

    private void hideImage() {
        MyUtils.hide(imageView);
    }

    private void hideVideo() {
        if (null != mYoutubePlayerView)
            mYoutubePlayerView.release();

        this.videoContainer.removeAllViews();

        MyUtils.hide(videoThrobber);
    }

    private void hideAudio() {
        hideVideo();
    }


    private void showImageError(String errorMsg) {
        imageView.setImageResource(R.drawable.ic_image_error);
        MyUtils.showCustomToast(imageView.getContext(), errorMsg);
    }
}
