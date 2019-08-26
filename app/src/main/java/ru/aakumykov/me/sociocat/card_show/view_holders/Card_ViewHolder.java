package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.myimageloader.MyImageLoader;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class Card_ViewHolder extends Base_ViewHolder implements
        iCardShow.iCard_ViewHolder,
        TagView.OnTagClickListener
{
    @BindView(R.id.cardLayout) LinearLayout cardLayout;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageContainer) FrameLayout imageContainer;
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

    private static final String TAG = "Card_ViewHolder";
    // TODO: опасненько его здесь хранить!
    private Context context;
    private iCardShow.iCardPresenter cardPresenter;


    // Конструктор
    public Card_ViewHolder(View itemView, iCardShow.iCardPresenter cardPresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        this.cardPresenter = cardPresenter;
    }

    public void initialize(Card card) {
            tagsContainer.setOnTagClickListener(this);
            displayCard(card);
    }


    // Нажатия
    @OnClick(R.id.authorView)
    void onAuthorClicked() {
        cardPresenter.onAuthorClicked();
    }

    @OnClick(R.id.replyWidget)
    void onReplyWidgetClicked() {
        cardPresenter.onReplyClicked();
    }

    @OnClick(R.id.cardRatingUpButton)
    void onCardRatingUpClicked() {
        cardPresenter.onRatingUpClicked(this);
    }

    @OnClick(R.id.cardRatingDownButton)
    void onCardRatingDownClicked() {
        cardPresenter.onRatingDownClicked(this);
    }



    // iCard_ViewHolder
    @Override
    public void showRatingThrobber() {
        disableRatingButtons();

        MyUtils.hide(cardRatingView);
        MyUtils.show(cardRatingThrobber);
    }

    @Override
    public void hideRatingThrobber() {
        enableRatingContols();

        MyUtils.show(cardRatingView);
        MyUtils.hide(cardRatingThrobber);
    }

    @Override
    public void disableRatingButtons() {
        MyUtils.disable(cardRatingUpButton);
        MyUtils.disable(cardRatingDownButton);
    }

    @Override
    public void enableRatingContols() {
        MyUtils.enable(cardRatingUpButton);
        MyUtils.enable(cardRatingDownButton);
    }

    @Override
    public void showRating(Card card, @Nullable String ratedByUserId) {
        Log.d(TAG, card.toString());

        int thumbUpImageResource = R.drawable.ic_thumb_up_neutral;
        int thumbDownImageResource = R.drawable.ic_thumb_down_neutral;

        if (null != ratedByUserId) {
            if (card.isRatedUpBy(ratedByUserId) && !card.isRatedDownBy(ratedByUserId))
                thumbUpImageResource = R.drawable.ic_thumb_up_colored;
            else if (!card.isRatedUpBy(ratedByUserId) && card.isRatedDownBy(ratedByUserId))
                thumbDownImageResource = R.drawable.ic_thumb_down_colored;
        }

        hideRatingThrobber();
        cardRatingView.setText(String.valueOf(card.getRating()));
        cardRatingUpButton.setImageResource(thumbUpImageResource);
        cardRatingDownButton.setImageResource(thumbDownImageResource);
    }


    // TagView.OnTagClickListener
    @Override
    public void onTagClick(int position, String text) {
        List<String> allTags = tagsContainer.getTags();
        String tag = allTags.get(position);
        cardPresenter.onTagClicked(tag);
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
    private void displayCard(Card card) {
        String currentUserId = AuthSingleton.currentUserId();

        showTitle(card);
        showMainContent(card);
        showQuoteSource(card);
        showDescription(card);
        showAuthor(card);
        showTime(card.getCTime(), card.getMTime());
        showTags(card.getTagsHash());
        showRating(card, currentUserId);
    }

    private void showTitle(Card card) {
        titleView.setText(card.getTitle());
    }

    private void showMainContent(Card card) {
        switch (card.getType()) {

            case Constants.TEXT_CARD:
                String quote = MyUtils.getString(quoteView.getContext(), R.string.aquotes, card.getQuote());
                if (!TextUtils.isEmpty(quote)) {
                    quoteView.setText(quote);
                    MyUtils.show(quoteView);
                }
                break;

            case Constants.IMAGE_CARD:
                MyImageLoader.loadImageToContainer(
                        context,
                        imageContainer,
                        card.getImageURL()
                );
                break;

            case Constants.VIDEO_CARD:
                Float timecode = card.getTimecode();
                showYoutubeMedia(card.getVideoCode(), card.getTimecode(), InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER);
                break;

            case Constants.AUDIO_CARD:
                showYoutubeMedia(card.getAudioCode(), card.getTimecode(), InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER);
                break;
        }
    }

    private void showQuoteSource(Card card) {
        String source = card.getQuoteSource();
        if (!TextUtils.isEmpty(source)) {
            quoteSourceView.setText(source);
            MyUtils.show(quoteSourceView);
        }
    }

    private void showDescription(Card card) {
        descriptionView.setText(card.getDescription());
    }

    private void showAuthor(Card card) {
        String authorString = context.getString(R.string.CARD_SHOW_author, card.getUserName());
        authorView.setText(authorString);
    }

    private void showTime(Long cTime, Long mTime) {
        Long currentTime = System.currentTimeMillis();

        if (0L != cTime) {
            CharSequence createTime = DateUtils.getRelativeTimeSpanString(cTime, currentTime, DateUtils.SECOND_IN_MILLIS);
            String fullCreateTime = context.getString(R.string.CARD_SHOW_created, createTime);
            cTimeView.setText(fullCreateTime);
            MyUtils.show(cTimeView);
        }

        if (0L != mTime && !cTime.equals(mTime)) {
            CharSequence editTime = DateUtils.getRelativeTimeSpanString(mTime, currentTime, DateUtils.SECOND_IN_MILLIS);
            String fullEditTime = context.getString(R.string.CARD_SHOW_edited, editTime);
            mTimeView.setText(fullEditTime);
            MyUtils.show(mTimeView);
        }
    }

    private void showTags(HashMap<String,Boolean> tagsHash) {
        if (null != tagsHash) {
            List<String> tagsList = new ArrayList<>(tagsHash.keySet());
            tagsContainer.setTags(tagsList);
            MyUtils.show(tagsContainer);
        }
    }

    private void showYoutubeMedia(String mediaCode, @Nullable Float timecode, InsertableYoutubePlayer.PlayerType playerType) {

        int waitingMessageId = -1;
        switch (playerType){
            case VIDEO_PLAYER:
                waitingMessageId = R.string.YOUTUBE_PLAYER_waiting_for_video;
                break;
            case AUDIO_PLAYER:
                waitingMessageId = R.string.YOUTUBE_PLAYER_waiting_for_audio;
                break;
            default:
                throw new RuntimeException("Wrong player type: "+playerType);
        }

        // TODO: showVideo(), showAudio()

        InsertableYoutubePlayer insertableYoutubePlayer = new InsertableYoutubePlayer(
                context,
                videoContainer,
                waitingMessageId
        );

        Float timecodeFloat = null;
        if (null != timecode)
            timecodeFloat = new BigDecimal(timecode).floatValue();

        insertableYoutubePlayer.show(mediaCode, timecodeFloat, playerType);
    }

    private void onReplyWidgetClicked(Card card) {
        // Это не контроллера ответственность!
        //        cardController.showCommentForm(card);
    }
}
