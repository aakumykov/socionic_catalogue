package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import ru.aakumykov.me.sociocat.R;

public class MyYoutubePlayer implements
        View.OnClickListener
{
    public interface iMyYoutubePlayerCallbacks {
        void onMediaAdded();
    }

    public enum PlayerType {
        VIDEO_PLAYER, AUDIO_PLAYER
    }

    private PlayerType playerType;
    private int waitingMessageId;
    private int playIconId;
    private int pauseIconId;
    private int waitIconId;
    private Context context;
    private ViewGroup targetContainer;

    LinearLayout player_layout_xml;
    private TextView playerMsg;
    private ConstraintLayout audioPlayer;
    private ImageView playerControlButton;
    private SeekBar playerSeekBar;
    private TextView playerStatusBar;

    private YouTubePlayerView videoPlayer;
    private YouTubePlayer player;

    private float videoDuration = 0f;
    private String videoId;
    private PlayerConstants.PlayerState mediaPlayerState;


    public MyYoutubePlayer(
            @NonNull Context context,
            @NonNull ViewGroup targetContainer,
            int waitingMessageId,
            int playIconId,
            int pauseIconId,
            int waitIconId
    )
    {
        this.context = context;
        this.targetContainer = targetContainer;
        this.waitingMessageId = waitingMessageId;
        this.playIconId = playIconId;
        this.pauseIconId = pauseIconId;
        this.waitIconId = waitIconId;

        LayoutInflater layoutInflater = LayoutInflater.from(targetContainer.getContext());

        player_layout_xml = (LinearLayout) layoutInflater.inflate(R.layout.my_youtube_player, null);
        playerMsg = player_layout_xml.findViewById(R.id.playerMsg);
        videoPlayer = player_layout_xml.findViewById(R.id.videoPlayer);
        audioPlayer = player_layout_xml.findViewById(R.id.audioPlayer);
        playerControlButton = player_layout_xml.findViewById(R.id.playerControlButton);
        playerSeekBar = player_layout_xml.findViewById(R.id.playerSeekBar);
        playerStatusBar = player_layout_xml.findViewById(R.id.playerStatusBar);

        targetContainer.addView(player_layout_xml);

        preparePlayer();
    }


    public void show(String videoId, PlayerType playerType) {
        this.videoId = videoId;
        this.playerType = playerType;

        if (null != player)
            player.cueVideo(videoId, 0.0f);

        switch (playerType) {
            case AUDIO_PLAYER:
                MyUtils.hide(videoPlayer);
                MyUtils.show(audioPlayer);
                break;
            case VIDEO_PLAYER:
                MyUtils.hide(audioPlayer);
                MyUtils.show(videoPlayer);
                break;
        }
    }

    public void hide() {
        if (null != player)
            player.pause();
        MyUtils.hide(videoPlayer);
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
        if (null != videoPlayer)
            videoPlayer.release();
    }

    public void convert2video() {
//        playerType = PlayerType.VIDEO_PLAYER;
//        MyUtils.show(videoPlayer);
//        MyUtils.hide(audioControlsContainer);
//        playerContainer.setBackground(null);
    }

    public void convert2audio() {
//        playerType = PlayerType.AUDIO_PLAYER;
//        MyUtils.hide(videoPlayer);
//        MyUtils.show(audioControlsContainer);
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public boolean hasMedia() {
        return !TextUtils.isEmpty(videoId);
    }

    public boolean wasPlay() {
        return PlayerConstants.PlayerState.PLAYING.equals(mediaPlayerState);
    }

    public boolean isAudioPlayer() {
        return PlayerType.AUDIO_PLAYER.equals(playerType);
    }

    public boolean isVideoPlayer() {
        return PlayerType.VIDEO_PLAYER.equals(playerType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playerControlButton:
                playPauseMedia();
                break;
            default:
                break;
        }
    }



    private void preparePlayer() {

        setPlayerMsg(waitingMessageId, true);

        videoPlayer.initialize(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                player = youTubePlayer;

                if (null != videoId)
                    youTubePlayer.cueVideo(videoId, 0f);

                hidePlayerMsg();
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
                mediaPlayerState = playerState;
                //setPlayerMsg(state);
                if (isAudioPlayer())
                    changePlayerControls(mediaPlayerState);
            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {
                setPlayerMsg(String.valueOf(playerError), false);
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
                moveSeekBar(v);
            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {
                videoDuration = v;
            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        });

        playerControlButton.setOnClickListener(this);
    }

    private void showExistingVideo() {
        player.cueVideo(videoId, 0.0f);
    }

    private void moveSeekBar(float currentPosition) {
        int progress = Math.round((currentPosition / videoDuration) * 100);
        playerSeekBar.setProgress(progress);
    }

    private <T> void setPlayerMsg(T arg, boolean withAnimation) {

        int duration = 1000;

        Animation fadeOut = new AlphaAnimation(1.0f, 0.5f);
        fadeOut.setDuration(duration);
        fadeOut.setRepeatCount(Animation.INFINITE);

        Animation fadeIn = new AlphaAnimation(0.5f, 1.0f);
        fadeIn.setDuration(duration);
        fadeIn.setRepeatCount(Animation.INFINITE);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        animationSet.setRepeatMode(Animation.RESTART);

        if (withAnimation)
            playerMsg.startAnimation(animationSet);

        String msg = "";

        if (arg instanceof Integer) {
            int msgId = (Integer)arg;
            msg = context.getResources().getString(msgId);
        } else {
            msg = String.valueOf(arg);
        }

        playerMsg.setText(msg);
        //playerMsg.setVisibility(View.VISIBLE);
    }

    private void hidePlayerMsg() {
        playerMsg.clearAnimation();
        MyUtils.hide(playerMsg);
    }

    private void playPauseMedia() {
        if (PlayerConstants.PlayerState.PLAYING.equals(this.mediaPlayerState)) {
            player.pause();
        } else {
            player.play();
        }
    }

    private void changePlayerControls(PlayerConstants.PlayerState state) {
        switch (state) {
            case PLAYING:
                showPauseButton();
                break;
            case PAUSED:
                showPlayButton();
            case BUFFERING:
                showWatingButton();
                break;
            default:
                break;
        }
    }

    private void showPlayButton() {
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_player_play);
        playerControlButton.setImageDrawable(icon);
    }

    private void showPauseButton() {
        Drawable icon = context.getResources().getDrawable(pauseIconId);
        playerControlButton.setImageDrawable(icon);
    }

    private void showWatingButton() {
        Drawable icon = context.getResources().getDrawable(waitIconId);
        playerControlButton.setImageDrawable(icon);
    }


}