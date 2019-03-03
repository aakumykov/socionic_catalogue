package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import ru.aakumykov.me.sociocat.R;

import static android.widget.LinearLayout.VERTICAL;

public class MyYoutubePlayer implements
        View.OnClickListener
{
    public enum PlayerType {
        VIDEO_PLAYER, AUDIO_PLAYER
    }

    private static class PlayerConfig {
        static final int seekBarTopMargin = 30;
        static final int seekBarBottomMargin = 30;

    }

    private final static String PLAY_PAUSE_BUTTON_TAG = "playPauseButton";

    private PlayerType playerType;
    private LinearLayout playerContainer;
    private TextView videoMsg;
    private YouTubePlayerView youTubePlayerView;
    private LinearLayout controlsContainer;
    private ImageView playPauseButton;
    private SeekBar seekBar;
    private YouTubePlayer player;
    private PlayerConstants.PlayerState playerState;
    private float videoDuration = 0;
    private Context context;
    private ViewGroup targetContainer;
    private View viewToInsertAfter;
    private String videoId;

    public MyYoutubePlayer(
            PlayerType playerType,
            @NonNull Context context,
            @NonNull ViewGroup targetContainer,
            @Nullable View viewToInsertAfter
    )
    {
        this.playerType = playerType;
        this.context = context;
        this.targetContainer = targetContainer;
        this.viewToInsertAfter = viewToInsertAfter;

        preparePlayerContainer();
        preparePlayerMsg();
        if (PlayerType.AUDIO_PLAYER.equals(playerType))
            preparePlayerControls();
        attachPlayerToTargetContainer();
    }


    public void setVideo(String videoId, boolean showPlayerImmediately) {
        this.videoId = videoId;
        if (showPlayerImmediately) {
            if (null != player)
                player.cueVideo(videoId, 0f);
            else
                prepareAndShowPlayer();
        }

    }

    public void show() {
        prepareAndShowPlayer();
    }

    public void pause() {
        if (null != player)
            player.pause();
    }

    public void play() {
        if (null != player)
            player.play();
    }

    public void release() {
        if (null != youTubePlayerView)
            youTubePlayerView.release();
    }


    private void preparePlayerContainer() {
        playerContainer = new LinearLayout(context);
        playerContainer.setOrientation(VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        playerContainer.setLayoutParams(layoutParams);
    }

    private void prepareAndShowPlayer() {

        if (null != youTubePlayerView)
            return;

        showPlayerMsg(R.string.YOUTUBE_PLAYER_wating_for_video);

        youTubePlayerView = new YouTubePlayerView(context);

        if (PlayerType.AUDIO_PLAYER.equals(playerType))
            youTubePlayerView.setVisibility(View.GONE);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer youTubePlayer) {
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        player = youTubePlayer;
                        if (null != videoId)
                            youTubePlayer.cueVideo(videoId, 0f);
                        playerContainer.addView(youTubePlayerView, playerContainer.indexOfChild(videoMsg));
                        hidePlayerMsg();
                        showPlayerControls();
                    }
                    @Override
                    public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
                        super.onStateChange(state);
                        playerState = state;
//                        showPlayerMsg(state);
                        changePlayerControls(state);
                    }
                    @Override
                    public void onVideoDuration(float duration) {
                        super.onVideoDuration(duration);
                        videoDuration = duration;
                    }
                    @Override
                    public void onCurrentSecond(float second) {
                        super.onCurrentSecond(second);
                        moveSeekBar(second);
                    }
                    @Override
                    public void onError(@NonNull PlayerConstants.PlayerError error) {
                        super.onError(error);
                        showPlayerMsg(String.valueOf(error));
                    }
                });
            }
        }, true);
    }

    private void attachPlayerToTargetContainer() {
        int index = (null == viewToInsertAfter) ? 0 : targetContainer.indexOfChild(viewToInsertAfter) + 1;
        targetContainer.addView(playerContainer, index);
    }


    private void preparePlayerControls() {

        // Контейнер элементов управления
        controlsContainer = new LinearLayout(context);
        controlsContainer.setOrientation(LinearLayout.HORIZONTAL);
        controlsContainer.setVisibility(View.GONE);

        // Кнопка играть/остановить
        playPauseButton = new ImageView(context);
//        playPauseButton.setId(R.id.playPauseButton);
        playPauseButton.setTag("playPauseButton");
        playPauseButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
        playPauseButton.setMinimumWidth(64);
        LinearLayout.LayoutParams ppLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        playPauseButton.setLayoutParams(ppLP);
        playPauseButton.setOnClickListener(this);

        // Строка прокрутки
        seekBar = new SeekBar(context);
        LinearLayout.LayoutParams sbLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        seekBar.setLayoutParams(sbLP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(videoDuration * (progress/100f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Сборка воедино
        controlsContainer.addView(playPauseButton);
        controlsContainer.addView(seekBar);

        // Прикрепление к контейнеру выигрывателя
        playerContainer.addView(controlsContainer);
        setMargins(controlsContainer, 0, PlayerConfig.seekBarTopMargin,0, PlayerConfig.seekBarBottomMargin);
    }

    private void showPlayerControls() {
        if (null != controlsContainer)
            controlsContainer.setVisibility(View.VISIBLE);
    }

    private void moveSeekBar(float currentPosition) {
        if (null != seekBar) {
            int progress = Math.round((currentPosition / videoDuration) * 100);
            seekBar.setProgress(progress);
        }
    }


    private void preparePlayerMsg() {
        videoMsg = new TextView(context);
        videoMsg.setText("Медиа выигрыватель");
        videoMsg.setTextColor(context.getResources().getColor(android.R.color.white));
        videoMsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        videoMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        videoMsg.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        videoMsg.setVisibility(View.GONE);
        playerContainer.addView(videoMsg);
    }

    private <T> void showPlayerMsg(T arg) {
        String msg = "";
        if (arg instanceof Integer) {
            int msgId = (Integer)arg;
            msg = context.getResources().getString(msgId);
        } else {
            msg = String.valueOf(arg);
        }
        videoMsg.setText(msg);
        videoMsg.setVisibility(View.VISIBLE);
    }

    private void hidePlayerMsg() {
        videoMsg.setVisibility(View.GONE);
    }


    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onClick(View v) {
        if (PLAY_PAUSE_BUTTON_TAG.equals(v.getTag())) {
            playPauseMedia();
        }
    }

    private void playPauseMedia() {
        switch (playerState) {
            case PLAYING:
                player.pause();
                showPlayButton();
                break;
            default:
                player.play();
                showPauseButton();
                break;
        }
    }

    private void changePlayerControls(PlayerConstants.PlayerState state) {
        switch (state) {
            case PLAYING:
                showPauseButton();
                break;
            case PAUSED:
                showPlayButton();
//            case BUFFERING:
//                showWatingButton();
//                break;
            default:
                break;
        }
    }

    private void showPlayButton() {
        Drawable icon = context.getResources().getDrawable(android.R.drawable.ic_media_play);
        playPauseButton.setImageDrawable(icon);
    }

    private void showPauseButton() {
        Drawable icon = context.getResources().getDrawable(android.R.drawable.ic_media_pause);
        playPauseButton.setImageDrawable(icon);
    }

//    private void showWatingButton() {
//        Drawable icon = context.getResources().getDrawable(android.R.drawable.presence_away);
//        playPauseButton.setImageDrawable(icon);
//    }
}