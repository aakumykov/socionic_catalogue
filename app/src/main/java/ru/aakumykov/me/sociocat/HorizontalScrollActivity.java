package ru.aakumykov.me.sociocat;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
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
    @BindView(R.id.quoteView) TextView quoteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_scroll);
        ButterKnife.bind(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        quoteView.setText(
                "ширина: "+width+"\n"+
                "высота: "+height+"\n"+
                "пропорция: "+String.valueOf(width*1.0/height*1.0)
        );

        MyUtils.show(progressBar);

        String wideImage = "http://www.train-photo.ru/data/media/519/1res.jpg";
        String tallImage1 = "http://loispaigesimenson.com/wp-content/uploads/2015/09/Tall-1.png";
        String tallImage2 = "http://www.tallheights.com/wp-content/uploads/2016/06/background_purple.jpg";

        Picasso.get().load(wideImage)
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
