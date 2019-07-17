package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import ru.aakumykov.me.sociocat.card_show.presenters.iCardPresenter;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class Card_ViewHolder extends Base_ViewHolder implements
        TagView.OnTagClickListener
{
    @BindView(R.id.cardLayout) LinearLayout cardLayout;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageContainer) FrameLayout imageContainer;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;
    @BindView(R.id.descriptionView) TextView descriptionView;
    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;
    @BindView(R.id.authorView) TextView authorView;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.replyWidget) TextView replyWidget;

    private static final String TAG = "Card_ViewHolder";
    private Context context;
    private Card currentCard;
    private iCardPresenter cardPresenter;
    private boolean isInitialized = false;

    // Конструктор
    public Card_ViewHolder(View itemView, iCardPresenter cardPresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        this.cardPresenter = cardPresenter;
    }

    public void initialize(Card card) {
        if (!this.isInitialized) {
            this.currentCard = card;

            tagsContainer.setOnTagClickListener(this);

            this.isInitialized = true;
            displayCard(card);
        }
    }


    // Нажатия
    @OnClick(R.id.replyWidget)
    void onReplyWidgetClicked() {
        cardPresenter.onReplyClicked();
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
        showTitle(card);
        showDescribedContent(card);
        showDescription(card);
        showAuthor(card);
        showTime(card.getCTime(), card.getMTime());
        showTags(card.getTags());
    }

    private void showTitle(Card card) {
        titleView.setText(card.getTitle());
    }

    private void showDescribedContent(Card card) {
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
                showYoutubeMedia(card.getVideoCode(), InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER);
                break;

            case Constants.AUDIO_CARD:
                showYoutubeMedia(card.getAudioCode(), InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER);
                break;
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

    private void showYoutubeMedia(String mediaCode, InsertableYoutubePlayer.PlayerType playerType) {

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

        insertableYoutubePlayer.show(mediaCode, playerType);
    }

    private void onReplyWidgetClicked(Card card) {
        // Это не контроллера ответственность!
        //        cardController.showCommentForm(card);
    }
}
