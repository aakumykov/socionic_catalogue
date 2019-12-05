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
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Card_ViewHolder extends Base_ViewHolder implements iCard_ViewHolder {

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

    @BindView(R.id.cardRateUpWidget) ImageView cardRatingUpButton;
    @BindView(R.id.cardRateDownWidget) ImageView cardRatingDownButton;
    @BindView(R.id.cardRatingView) TextView cardRatingView;
    @BindView(R.id.cardRatingThrobber) ProgressBar cardRatingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;

    private enum MediaType { AUDIO, VIDEO }
    private iList_Item currentListItem = null;
    private Card currentCard = null;
    private iCardShow2.iPresenter presenter = null;


    public Card_ViewHolder(@NonNull View itemView, iCardShow2.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }


    // Base_ViewHolder
    @Override
    public void initialize(iList_Item listItem) {
        this.currentListItem = listItem;
        this.currentCard = (Card) listItem.getPayload();
        displayCard();
    }


    // iCardViewHolder
    @Override
    public void disableRatingControls() {
        MyUtils.disable(cardRatingUpButton);
        MyUtils.disable(cardRatingDownButton);

        MyUtils.hide(cardRatingView);
        MyUtils.show(cardRatingThrobber);
    }

    @Override
    public void enableRatingControls(int ratingValue) {
        MyUtils.enable(cardRatingUpButton);
        MyUtils.enable(cardRatingDownButton);

        cardRatingView.setText(String.valueOf(ratingValue));

        MyUtils.show(cardRatingView);
        MyUtils.hide(cardRatingThrobber);
    }


    // Нажатия
    @OnClick(R.id.replyWidget)
    void onReplyClicked() {
        presenter.onReplyClicked(currentListItem);
    }

    @OnClick(R.id.cardRateUpWidget)
    void onRateUpClicked() {
        presenter.onRateUpClicked(this);
    }

    @OnClick(R.id.cardRateDownWidget)
    void onRateDownClicked() {
        presenter.onRateDownClicked(this);
    }

    @OnClick(R.id.authorView)
    void onAuthorClicked() {
        presenter.onAuthorClicked();
    }


    // Внутренние методы
    private void displayCard() {

        titleView.setText(currentCard.getTitle());
        descriptionView.setText(currentCard.getDescription());

        String quoteSource = currentCard.getQuoteSource();
        if (null != quoteSource) {
            quoteSourceView.setText(quoteSource);
        }

        switch (currentCard.getType()) {
            case Card.TEXT_CARD:
                quoteView.setText(currentCard.getQuote());
                break;

            case Card.IMAGE_CARD:
                Glide.with(imageView)
                        .load(currentCard.getImageURL())
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

        authorView.setText(
                MyUtils.getString(
                        authorView.getContext(),
                        R.string.CARD_SHOW_author,
                        currentCard.getUserName()
                )
        );
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
