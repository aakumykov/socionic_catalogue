package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends Base_ViewHolder {

//    @BindView(R.id.cardLayout) LinearLayout cardLayout;

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;
    @BindView(R.id.quoteSourceView) TextView quoteSourceView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.authorView) TextView authorView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    @BindView(R.id.cardRatingUpButton) ImageView cardRatingUpButton;
    @BindView(R.id.cardRatingDownButton) ImageView cardRatingDownButton;
    @BindView(R.id.cardRatingView) TextView cardRatingView;
    @BindView(R.id.cardRatingThrobber) ProgressBar cardRatingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;

    private enum MediaType { AUDIO, VIDEO }
    private Card currentCard = null;


    public Card_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        Card card = (Card) listItem.getPayload();
        displayCard(card);
    }

    private void displayCard(Card card) {

        currentCard = card;

        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());

        String quoteSource = card.getQuoteSource();
        if (null != quoteSource) {
            quoteSourceView.setText(quoteSource);
        }

        switch (card.getType()) {
            case Card.TEXT_CARD:
                quoteView.setText(card.getQuote());
                break;

            case Card.IMAGE_CARD:
                Glide.with(imageView)
                        .load(card.getImageURL())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .into(imageView);
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
    }

    private void displayMedia(MediaType mediaType) {

        InsertableYoutubePlayer insertableYoutubePlayer =
                new InsertableYoutubePlayer(videoContainer.getContext(), videoContainer);

        switch (mediaType) {
            case AUDIO:
                insertableYoutubePlayer.show(
                        currentCard.getAudioCode(),
                        currentCard.getTimecode(),
                        InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER,
                        R.string.YOUTUBE_PLAYER_waiting_for_audio
                );
                break;

            case VIDEO:
                insertableYoutubePlayer.show(
                        currentCard.getVideoCode(),
                        currentCard.getTimecode(),
                        InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER,
                        R.string.YOUTUBE_PLAYER_waiting_for_video
                );
                break;

            default:
                throw new RuntimeException("Unknown mediaType: "+mediaType);
        }
    }
}
