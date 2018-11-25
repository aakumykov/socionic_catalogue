package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.utils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class YoutubeVideo extends AppCompatActivity {

    @BindView(R.id.videoPlayerThrobber) ProgressBar videoPlayerThrobber;
    @BindView(R.id.youtube_player_view) YouTubePlayerView youTubePlayerView;
    @BindView(R.id.videoCodeInput) EditText videoCodeInput;
    @BindView(R.id.playButton) Button playButton;
    @BindView(R.id.removeButton) Button removeButton;

    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video);
        ButterKnife.bind(this);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer = initializedYouTubePlayer;
                    }
                });
            }
        }, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.videoCodeInput)
    void onClickEditVield() {
        videoCodeInput.selectAll();
    }

    @OnClick(R.id.playButton)
    void playVideo() {
        String videoCode = MVPUtils.extractYoutubeVideoCode(videoCodeInput.getText().toString());

        youTubePlayer.loadVideo(videoCode, 0f);
        youTubePlayerView.setVisibility(View.VISIBLE);

        videoCodeInput.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.removeButton)
    void removeVideo() {
        youTubePlayer.pause();
        youTubePlayerView.setVisibility(VideoView.GONE);

        removeButton.setVisibility(View.GONE);
        videoCodeInput.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
    }


}
