package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import butterknife.BindView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.AnimationUtils;

public class CardViewHolder_Feed extends CardViewHolder {

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.videoThrobber) ImageView videoThrobber;
    @BindView(R.id.audioVideoContainer) FrameLayout audioVideoContainer;

    private YouTubePlayerView mYoutubePlayerView;


    public CardViewHolder_Feed(@NonNull View itemView) {
        super(itemView);
    }


    @Override
    public void initialize(BasicMVP_ListItem basicListItem) {
        super.initialize(basicListItem);

        Card card = extractCardFromListItem(basicListItem);
        if (null == card) {
            showNoCardError();
            return;
        }

        displayCard(card);
    }

    @Override
    protected void showNoCardError() {

    }

    @Override
    protected void displayCard(@NonNull Card card) {
        super.displayCard(card);

        if (card.isImageCard())
            showImage(card.getImageURL());
        else
            hideImage();

        if (card.isTextCard())
            showQuote(card);
        else
            hideQuote();

        if (card.isVideoCard())
            showVideo(card);
        else
            hideVideo();

        if (card.isAudioCard())
            showAudio(card);
        else
            hideAudio();
    }



    private void showImage(String imageURL) {

        imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        ViewUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        Glide.with(imageView.getContext())
                .load(imageURL)
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

    private void hideImage() {
        ViewUtils.hide(imageView);
    }


    private void showQuote(@NonNull Card card) {
        String quote = card.getQuote();

        int cutIndex = Math.min(quote.length(), Constants.CARDS_FEED_QUOTE_MAX_LENGTH);

        quote = card.getQuote().substring(0, cutIndex);

        quoteView.setText(quote);
        ViewUtils.show(quoteView);
    }

    private void hideQuote() {
        ViewUtils.hide(quoteView);
    }


    private void showVideo(@NonNull Card card) {

        ViewUtils.show(videoThrobber);
        videoThrobber.startAnimation(AnimationUtils.createFadeInOutAnimation(750L, false));

        if (null != mYoutubePlayerView)
            mYoutubePlayerView.release();

        mYoutubePlayerView = new YouTubePlayerView(audioVideoContainer.getContext());
        mYoutubePlayerView.setEnableAutomaticInitialization(false);
        mYoutubePlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mYoutubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                videoThrobber.clearAnimation();
                ViewUtils.hide(videoThrobber);

                audioVideoContainer.addView(mYoutubePlayerView);
                ViewUtils.show(audioVideoContainer);

                youTubePlayer.cueVideo(card.getVideoCode(), card.getTimecode());
            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);

                videoThrobber.clearAnimation();
                videoThrobber.setImageResource(R.drawable.ic_youtube_video_error);
            }
        });

    }

    private void hideVideo() {
        audioVideoContainer.removeAllViews();
    }


    private void showAudio(@NonNull Card card) {

    }

    private void hideAudio() {

    }

}
