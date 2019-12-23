package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

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
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Card_ViewHolder extends Base_ViewHolder implements
        iCard_ViewHolder,
        TagView.OnTagClickListener
{
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

    @BindView(R.id.cardRateUpWidget) ImageView cardRatingUpWidget;
    @BindView(R.id.cardRateDownWidget) ImageView cardRatingDownWidget;
    @BindView(R.id.cardRatingView) TextView cardRatingView;
    @BindView(R.id.cardRatingThrobber) ProgressBar cardRatingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;

    private enum MediaType { AUDIO, VIDEO }
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

        displayAuthor();

        displayTags();

        cardRatingView.setText(String.valueOf(currentCard.getRating()));

        presenter.onCardAlmostDisplayed(this);
    }

    private void displayQuoteSource() {
        String quoteSource = currentCard.getQuoteSource();
        if (!TextUtils.isEmpty(quoteSource)) {
            quoteSourceView.setText(MyUtils.getString(quoteSourceView.getContext(), R.string.CARD_SHOW_quote_source, quoteSource));
            MyUtils.show(quoteSourceView);
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

    private void displayAuthor() {
        authorView.setText(
                MyUtils.getString(
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
}
