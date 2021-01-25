package ru.aakumykov.me.sociocat.b_cards_list.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import io.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.models.Card;

public class CardViewHolder_Grid extends CardViewHolder {

    @BindView(R.id.mediaView) ImageView imageView;
    @BindView(R.id.quoteView) TextView quoteView;

    public CardViewHolder_Grid(@NonNull View itemView) {
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
        resetSpecificParts();
        displaySpecificParts(card);
    }


    @Override
    protected void showNoCardError() {

    }

    @Override
    public void displayIsChecked(boolean isChecked) {
        ViewUtils.setVisibility(checkingOverlay, isChecked);
    }

    private void resetSpecificParts() {
        imageView.setImageDrawable(null);
        ViewUtils.hide(imageView);

        quoteView.setText("");
        ViewUtils.hide(quoteView);
    }

    private void displaySpecificParts(Card card) {
        switch (card.getType()) {
            case Card.IMAGE_CARD:
                displayImageCard(card);
                break;
            case Card.TEXT_CARD:
                displayTextCard(card);
                break;
            case Card.VIDEO_CARD:
                displayVideoCard(card);
                break;
            case Card.AUDIO_CARD:
                displayAudioCard(card);
                break;
            default:
                displayUnknownCard();
                break;
        }
    }

    private void displayImageCard(@NonNull Card card) {

        /*imageView.setImageResource(R.drawable.ic_image_placeholder_smaller);
        ViewUtils.show(imageView);
        AnimatorSet animatorSet = AnimationUtils.animateFadeInOut(imageView);

        Glide.with(imageView.getContext())
                .load(card.getImageURL())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (card.isImageCard()) {
//                            hideQuote();
//                            hideVideo();
//                            hideAudio();

                            imageView.setImageDrawable(resource);
                            AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                        }
//                        else
//                            hideImage();

                        return true;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (card.isImageCard()) {
                            AnimationUtils.revealFromCurrentAlphaState(imageView, animatorSet);
                            imageView.setImageResource(R.drawable.ic_image_error);
                        }
                        else {
//                            hideImage();
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
                });*/

        ViewUtils.show(imageView);

        Glide.with(elementView.getContext())
                .load(card.getImageURL())
                .placeholder(R.drawable.ic_image_placeholder_smaller)
                .error(R.drawable.ic_image_error)
                .into(imageView);
    }

    private void displayTextCard(@NonNull Card card) {
        String quote = card.getQuote();
        String quotePart = quote.substring(0, Math.min(quote.length(), AppConfig.CARDS_GRID_QUOTE_LENGTH));
        quoteView.setText(quotePart);
        ViewUtils.show(quoteView);
    }

    private void displayVideoCard(@NonNull Card card) {
        imageView.setImageResource(R.drawable.ic_card_type_youtube_video_list_mode);
        ViewUtils.show(imageView);
    }

    private void displayAudioCard(@NonNull Card card) {
        imageView.setImageResource(R.drawable.ic_card_type_audio_list_mode);
        ViewUtils.show(imageView);
    }

    private void displayUnknownCard() {
        imageView.setImageResource(R.drawable.ic_card_type_unknown);
        ViewUtils.show(imageView);
    }
}
