package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.myimageloader.MyImageLoader;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardController;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class Card_ViewHolder extends Base_ViewHolder
{
    @BindView(R.id.cardLayout) LinearLayout cardLayout;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageContainer) FrameLayout imageContainer;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;
    @BindView(R.id.descriptionView) TextView descriptionView;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    private static final String TAG = "Card_ViewHolder";
    private iCardController cardController;
    private Context context;
    private boolean isInitialized = false;

    // Конструктор
    public Card_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        Log.d(TAG, "new Card_ViewHolder()");
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    public void initialize(Card card, iCardController cardController) {
        if (!this.isInitialized) {
            Log.d(TAG, "initialize()");
            this.isInitialized = true;
            this.cardController = cardController;
            showCardContent(card);
        }
    }


    // Внутренние методы
    private void showCardContent(Card card) {

        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());

        switch (card.getType()) {

            case Constants.TEXT_CARD:
                String quote = card.getQuote();
                if (!TextUtils.isEmpty(quote)) {
                    quoteView.setText(quote);
                    MyUtils.show(quoteView);
                }
                break;

            case Constants.IMAGE_CARD:
                MyImageLoader.loadImageToContainer(
                        context,
                        imageContainer,
                        card.getImageURL(),
                        R.drawable.ic_image_error
                );
                break;

            case Constants.VIDEO_CARD:
                showYoutubeMedia(card.getVideoCode(), InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER);
                break;

            case Constants.AUDIO_CARD:
                showYoutubeMedia(card.getAudioCode(), InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER);
                break;
        }

        displayTags(card.getTags());
    }

    private void displayTags(HashMap<String,Boolean> tagsHash) {
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

    private void openCommentForm(Card card) {
        cardController.showCommentForm(card);
    }
}
