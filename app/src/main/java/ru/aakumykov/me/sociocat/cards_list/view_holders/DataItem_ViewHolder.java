package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.AnimationUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DataItem_ViewHolder
        extends BasicViewHolder
        implements View.OnLongClickListener
{
    @BindView(R.id.elementView) ViewGroup elementView;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.quoteView) TextView quoteView;
    @Nullable @BindView(R.id.imageView) ImageView imageView;
    @Nullable @BindView(R.id.authorView) TextView authorView;
    @Nullable @BindView(R.id.dateView) TextView dateView;
    @Nullable @BindView(R.id.commentsCountView) TextView commentsCountView;
    @Nullable @BindView(R.id.ratingView) TextView ratingView;
    @Nullable @BindView(R.id.cardTypeImageView) ImageView cardTypeImageView;

    private static final String TAG = DataItem_ViewHolder.class.getSimpleName();
    private DataItem dataItem;
    private final iCardsList.ViewMode currentViewMode;
    private final int neutralStateColor = -1;
    private Card currentCard;


    // Конструктор
    public DataItem_ViewHolder(View itemView, iCardsList.ViewMode viewMode) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        currentViewMode = viewMode;
    }


    // BasicViewHolder
    @Override
    public void initialize(ListItem listItem) {
        this.dataItem = (DataItem) listItem;
        this.currentCard = (Card) dataItem.getPayload();

        elementView.setOnLongClickListener(this);
        titleView.setOnLongClickListener(this);

        displayCardType();

        initializeCommonParts();

        switch (currentViewMode) {
            case FEED:
                initializeInFeedMode();
                break;
            case LIST:
                initializeInListMode();
                break;
            case GRID:
                initializeInGridMode();
                break;
            default:
                throw new RuntimeException("Unknown view mode: "+currentViewMode);
        }
    }

    private void displayCardType() {
        if (null != cardTypeImageView) {
            switch (currentCard.getType()) {
                case Card.TEXT_CARD:
                    cardTypeImageView.setImageResource(R.drawable.ic_card_type_text_list);
                    break;
                case Card.IMAGE_CARD:
                    cardTypeImageView.setImageResource(R.drawable.ic_card_type_image_list);
                    break;
                case Card.VIDEO_CARD:
                    cardTypeImageView.setImageResource(R.drawable.ic_card_type_video_list);
                    break;
                case Card.AUDIO_CARD:
                    cardTypeImageView.setImageResource(R.drawable.ic_card_type_audio_list);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setViewState(iCardsList.ItemState itemState) {
        switch (itemState) {
            case NEUTRAL:
                applyNeutralState();
                break;
            case SELECTED:
                applySelectedState();
                break;
            case DELETING:
                applyDeletingState();
                break;
            default:
                Log.e(TAG, "Unknown eViewHolderState: "+ itemState);
        }
    }


    // View.OnLongClickListener
    @Override
    public boolean onLongClick(View view) {
        if (presenter.canSelectItem()) {
            presenter.onDataItemLongClicked(dataItem);
        }
        return presenter.canSelectItem();
    }


    // Нажатия
    @Optional
    @OnClick({ R.id.elementView, R.id.titleView, R.id.imageView, R.id.quoteView, R.id.dateView })
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @Optional
    @OnClick({R.id.authorView})
    void onAuthorClicked() {
        presenter.onCardAuthorClicked(currentCard.getUserId());
    }

    @Optional
    @OnClick(R.id.commentsInfoContainer)
    void onCommentsClicked() {
        presenter.onCardCommentsClicked(currentCard);
    }

    @Optional
    @OnClick({R.id.rateUpWidget, R.id.rateDownWidget})
    void onRatingWidgetClicked() {
        presenter.onRatingWidgetClicked(currentCard);
    }



    // Внутренние
    private void initializeCommonParts() {
        titleView.setText(currentCard.getTitle());
    }

    private void initializeInFeedMode() {

        hideContentParts();

        showTitle();

        if (currentCard.isImageCard())
            showImage();

        if (currentCard.isTextCard()) {
            showQuote();
            MyUtils.hide(titleView);
        }

        if (currentCard.isVideoCard())
            showVideo();

        if (currentCard.isAudioCard())
            showAudio();

        showAuthor();

        showCommentsCount();

        showDate();

        showRating();
    }

    private void hideContentParts() {
        MyUtils.hide(quoteView);
        MyUtils.hide(imageView);
    }

    private void showTitle() {
        if (null != titleView) {
            titleView.setText(currentCard.getTitle());
            MyUtils.show(titleView);
        }
    }

    private void showQuote() {
        if (null != quoteView) {
            quoteView.setText(currentCard.getQuote());
            MyUtils.show(quoteView);
        }
    }

    private void showImage() {
        imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        MyUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        Glide.with(imageView).load(currentCard.getImageURL())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageView.setImageResource(R.drawable.ic_image_error);
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
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
                        imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                    }
                });
    }

    private void showVideo() {
        if (null == imageView)
            return;

        imageView.setImageResource(R.drawable.ic_youtube_video_placeholder);
        MyUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        String youtubeThumbnailURL = "https://img.youtube.com/vi/"+currentCard.getVideoCode()+"/mqdefault.jpg";

        Glide.with(imageView.getContext())
                .load(youtubeThumbnailURL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        imageView.setImageResource(R.drawable.ic_youtube_video_placeholder);
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

    private void showAudio() {
        /*if (null == audioVideoContainer)
            return;

        new InsertableYoutubePlayer(audioVideoContainer.getContext(), audioVideoContainer).show(
                currentCard.getAudioCode(), currentCard.getTimecode(), InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER
        );*/
    }

    private void showAuthor() {
        if (null != authorView)
            authorView.setText(currentCard.getUserName());
    }

    private void showCommentsCount() {
        if (null != commentsCountView)
            commentsCountView.setText( String.valueOf(currentCard.getCommentsKeys().size()) );
    }

    private void showRating() {
        if (null != ratingView)
            ratingView.setText(String.valueOf(currentCard.getRating()));
    }


    private void initializeInListMode() {
        //displayDate();
    }

    private void initializeInGridMode() {
        if (currentCard.isImageCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_image);
        }
        else if (currentCard.isTextCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_text);
        }
        else if (currentCard.isAudioCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_audio);
        }
        else if (currentCard.isVideoCard()) {
            imageView.setImageResource(R.drawable.ic_card_type_video);
        }
        else {
            imageView.setImageResource(R.drawable.ic_card_type_unknown);
        }
    }

    private void showDate() {
        Long cTime = currentCard.getCTime();
        Long mTime = currentCard.getMTime();

        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        String formatterDate = dateFormat.format((cTime > 0) ? cTime : mTime);

        dateView.setText(formatterDate);
    }


    private void applySelectedState() {
        int selectedColor = elementView.getResources().getColor(R.color.element_is_selected);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_selected);
                break;

            default:
                ((CardView) elementView).setCardBackgroundColor(selectedColor);
                break;
        }
    }

    private void applyDeletingState() {
        int deletingStateColor = elementView.getResources().getColor(R.color.element_is_now_deleting);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_deleting);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(deletingStateColor);
                break;
        }
    }

    private void applyNeutralState() {
        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_neutral);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(neutralStateColor);
                break;
        }
    }

}
