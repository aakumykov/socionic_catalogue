package io.gitlab.aakumykov.sociocat.b_cards_list.view_holders;

import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.utils.AnimationUtils;
import io.gitlab.aakumykov.sociocat.utils.ImageUtils;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;

public class CardViewHolder_Feed extends CardViewHolder {

    @BindView(R.id.mediaView) ImageView imageView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.videoThrobber) ImageView videoThrobber;
    @BindView(R.id.audioVideoContainer) FrameLayout audioVideoContainer;
    @BindView(R.id.authorView) TextView authorView;
    @BindView(R.id.dateView) TextView dateView;
    @BindView(R.id.commentsCountView) TextView commentsCountView;
    @BindView(R.id.ratingView) TextView ratingView;

    private final static String TAG = CardViewHolder_Feed.class.getSimpleName();
    public CardViewHolder_Feed(@NonNull View itemView) {
        super(itemView);
    }


    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {
        super.initialize(basicListItem);

        Card card = extractCardFromListItem(basicListItem);
        if (null == card) {
            showNoCardError();
            return;
        }

        displayCommonParts(card);
    }

    @Override
    protected void showNoCardError() {

    }

    @Override
    protected void displayCommonParts(@NonNull Card card) {
        super.displayCommonParts(card);

        showAuthor(card);
        showDate(card);
        showCommentsCount(card);
        showRating(card);

        if (card.isTextCard()) {
            showQuote(card);

            hideImage();
            hideVideo();
        }

        if (card.isImageCard()) {
            showImage(card);

            hideQuote();
            hideVideo();
            return;
        }

        if (card.isVideoCard()) {
            showVideoPreview(card);

            hideQuote();
        }

        if (card.isAudioCard()) {
            showAudio(card);

            hideImage();
            hideQuote();
        }
    }

    private void showAuthor(@NonNull Card card) {
        authorView.setText(card.getUserName());
    }

    private void showDate(@NonNull Card card) {
        dateView.setText(
                MyUtils.getHumanTimeAgo(
                        dateView.getContext(),
                        card.getCTime(),
                        R.string.simple_string
                )
        );
    }

    private void showCommentsCount(@NonNull Card card) {
        String text = TextUtils.getPluralString(commentsCountView.getContext(), R.plurals.comments_count, card.getCommentsKeys().size());
        commentsCountView.setText(text);
    }

    private void showRating(@NonNull Card card) {
        ratingView.setText(
                TextUtils.getText(ratingView.getContext(), R.string.CARDS_LIST_rating, card.getRating())
        );
    }

    private void showImage(@NonNull Card card) {

        imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        ViewUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        Glide.with(imageView.getContext())
                .load(card.getImageURL())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (card.isImageCard()) {
                            hideQuote();
                            hideVideo();
                            hideAudio();

                            imageView.setImageDrawable(resource);
                            AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        }
                        else
                            hideImage();

                        return true;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (card.isImageCard()) {
                            AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                            imageView.setImageResource(R.drawable.ic_image_error);
                        }
                        else {
                            hideImage();
                        }
                        return true;
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

        YouTubePlayerView youtubePlayerView = new YouTubePlayerView(audioVideoContainer.getContext());
        youtubePlayerView.setEnableAutomaticInitialization(false);
        youtubePlayerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                    if (card.isVideoCard()) {
                        hideQuote();
                        hideImage();

                        videoThrobber.clearAnimation();
                        ViewUtils.hide(videoThrobber);

                        audioVideoContainer.addView(youtubePlayerView);
                        ViewUtils.show(audioVideoContainer);

                        youTubePlayer.cueVideo(card.getVideoCode(), card.getTimecode());
                    }
                    else
                        hideVideo(youtubePlayerView);
            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                if (card.isVideoCard()) {
                    videoThrobber.clearAnimation();
                    videoThrobber.setImageResource(R.drawable.ic_youtube_video_error);
                }
            }
        });

    }

    private void showVideoPreview(@NonNull Card card) {

        imageView.setImageResource(R.drawable.ic_youtube_video_placeholder);
        ViewUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        String imageURL = "https://img.youtube.com/vi/"+card.getVideoCode()+"/mqdefault.jpg";

        Glide.with(imageView.getContext())
                .load(imageURL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap previewWithPlayMark = drawMarkOnVideoPreview(resource);
                        imageView.setImageBitmap(previewWithPlayMark);
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        return true;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageView.setImageResource(R.drawable.ic_youtube_video_error);
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        return true;
                    }
                })
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void hideVideo() {
        hideVideo(null);
    }

    private void hideVideo(@Nullable YouTubePlayerView youTubePlayerViewIfExists) {
        if (null != youTubePlayerViewIfExists)
            youTubePlayerViewIfExists.release();

        audioVideoContainer.removeAllViews();
    }


    private void showAudio(@NonNull Card card) {

    }

    private void hideAudio() {

    }

    private Bitmap drawMarkOnVideoPreview(@NonNull Drawable previewImageResource) {

        Bitmap backgroundBitmap = ((BitmapDrawable) previewImageResource).getBitmap();

        int markRectSize = backgroundBitmap.getWidth() / 5;

        Bitmap foregroundBitmap = ImageUtils.drawable2bitmap(
                itemView.getContext(),
                ImageUtils.getDrawableFromResource(itemView.getContext(), R.drawable.ic_video_mark_overlay),
                markRectSize,
                markRectSize
        );

        Bitmap resultBitmap = Bitmap.createBitmap(
                backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(),
                backgroundBitmap.getConfig()
        );

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, new Matrix(), null);
        canvas.drawBitmap(
                foregroundBitmap,
                (backgroundBitmap.getWidth() - foregroundBitmap.getWidth()) / 2,
                (backgroundBitmap.getHeight() - foregroundBitmap.getHeight()) / 2,
                new Paint()
        );

        return resultBitmap;
    }

}
