package ru.aakumykov.me.sociocat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class HorizontalScrollActivity extends AppCompatActivity {

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_scroll);
        ButterKnife.bind(this);

        MyUtils.show(progressBar);

        Picasso.get().load("http://www.train-photo.ru/data/media/519/1res.jpg")
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        MyUtils.hide(progressBar);
                        MyUtils.show(imageView);
                    }

                    @Override
                    public void onError(Exception e) {
                        MyUtils.hide(progressBar);
                        messageView.setText(e.getMessage());
                        MyUtils.show(messageView);
                    }
                });
    }


}
