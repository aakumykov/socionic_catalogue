package ru.aakumykov.me.mvp.yt_player;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ru.aakumykov.me.mvp.Config;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class YTPlayer implements
        iYTPlayer,
        YouTubePlayer.OnInitializedListener
{

    private final static String TAG = "YTPlayer";
    private YouTubePlayerView youTubePlayerView;
    private String videoId;
    private boolean isInitialized = false;

    YTPlayer(final YouTubePlayerView ytpv) {
        youTubePlayerView = ytpv;

    }

    // Интерфейсные методы
    @Override
    public void showPlayer() {
        MyUtils.show(youTubePlayerView);
    }

    @Override
    public void play(String videoId) {
        this.videoId = videoId;
        this.youTubePlayerView.initialize(Config.YOUTUBE_API_KEY, this);
    }

    // Системные методы
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        // TODO: обрабатывать 'wasRestored'
        youTubePlayer.loadVideo(this.videoId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
//        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
//        } else {
////            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
////            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//        }
    }


}
