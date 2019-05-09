package ru.aakumykov.me.sociocat.card_show_2.view_holders;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show_2.iCardController;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.MyYoutubePlayer;


public class Card_ViewHolder extends Base_ViewHolder {
    @BindView(R.id.cardLayout) LinearLayout cardLayout;

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    @BindView(R.id.imageContainer) FrameLayout imageContainer;
    @BindView(R.id.videoContainer) FrameLayout videoContainer;

    @BindView(R.id.likeWidget) ImageView likeWidget;
    @BindView(R.id.bookmarkWidget) ImageView bookmarkWidget;
    @BindView(R.id.shareWidget) ImageView shareWidget;
    @BindView(R.id.commentWidget) ImageView commentWidget;

    private iCardController cardController;
    private Context context;

    // Конструктор
    public Card_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = itemView.getContext();
    }

    public void initialize(Card card, iCardController cardController) {

        this.cardController = cardController;

        showCardContent(card);

        likeWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardController.likeCard(card, new iCardController.LikeCardCallbacks() {
                    @Override
                    public void onCardLikeSuccess() {
                        setCardLiked();
                    }

                    @Override
                    public void onCardLikeError(String errorMsg) {
                        MyUtils.showToast(cardController.getContext(), errorMsg);
                    }
                });
            }
        });

        commentWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentForm(card);
            }
        });
    }


    // Внутренние методы
    private void showCardContent(Card card) {

        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());

        switch (card.getType()) {

            case Constants.TEXT_CARD:
                quoteView.setText(card.getQuote());
                MyUtils.show(quoteView);
                break;

            case Constants.IMAGE_CARD:
                MyUtils.loadImageToContainer(
                        context,
                        imageContainer,
                        card.getImageURL(),
                        R.drawable.ic_image_error
                );
                break;

            case Constants.VIDEO_CARD:
                showYoutubeMedia(card.getVideoCode(), MyYoutubePlayer.PlayerType.VIDEO_PLAYER);
                break;

            case Constants.AUDIO_CARD:
                showYoutubeMedia(card.getAudioCode(), MyYoutubePlayer.PlayerType.AUDIO_PLAYER);
                break;
        }
    }

    private void showYoutubeMedia(String mediaCode, MyYoutubePlayer.PlayerType playerType) {

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

        MyYoutubePlayer myYoutubePlayer = new MyYoutubePlayer(
                context,
                videoContainer,
                waitingMessageId,
                R.drawable.ic_player_play,
                R.drawable.ic_player_pause,
                R.drawable.ic_player_wait
        );

        myYoutubePlayer.show(mediaCode, playerType);
    }

    private void setCardLiked() {
        likeWidget.setImageResource(R.drawable.ic_like_yes);
    }

    private void setCardUnliked() {
        likeWidget.setImageResource(R.drawable.ic_like_no);
    }

    private void openCommentForm(Card card) {
        cardController.showCommentForm(card);
    }
}
