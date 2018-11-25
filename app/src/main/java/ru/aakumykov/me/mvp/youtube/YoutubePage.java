package ru.aakumykov.me.mvp.youtube;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.utils.MVPUtils;

public class YoutubePage extends BaseView {

    @BindView(R.id.videoPlayerThrobber) ProgressBar videoPlayerThrobber;
    @BindView(R.id.youtube_player_view) YouTubePlayerView youTubePlayerView;
    @BindView(R.id.videoCodeInput) EditText videoCodeInput;
    @BindView(R.id.playButton) Button playButton;
    @BindView(R.id.removeButton) Button removeButton;

    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_video);
        ButterKnife.bind(this);

        Toast.makeText(this, "onCreate()", Toast.LENGTH_SHORT).show();

        activateUpButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    @OnClick(R.id.videoCodeInput)
    void onClickEditVield() {
        videoCodeInput.selectAll();
    }

    @OnClick(R.id.playButton)
    void playVideo() {
        final String videoCode = MVPUtils.extractYoutubeVideoCode(videoCodeInput.getText().toString());

        videoCodeInput.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);

        youTubePlayerView.setVisibility(View.VISIBLE);
        removeButton.setVisibility(View.VISIBLE);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer = initializedYouTubePlayer;
                        youTubePlayer.loadVideo(videoCode, 0f);
                    }
                });
            }
        }, true);
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
