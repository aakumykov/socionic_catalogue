package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;

public class CardView_View extends AppCompatActivity implements iCardView.View {

    private final static String TAG = "CardView_View";

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    iCardView.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_activity);
        ButterKnife.bind(this);

        presenter = new CardView_Presenter();

        Intent intent = getIntent();
        String cardKey = intent.getStringExtra(Constants.CARD_KEY);

        presenter.linkView(this); // нужно для отображения карточки!
        presenter.cardKeyRecieved(cardKey);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }


    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }

    @Override
    public void showImagePlaceholder() {
        imagePlaceholder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideImagePlaceholder() {
        imagePlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void showQuote() {
        quoteView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showImage() {
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void setQuote(String quote) {
        quoteView.setText(quote);
        MyUtils.show(quoteView);
    }

    @Override
    public void loadImage(String imageURL) {
        Log.d(TAG, "loadImage("+imageURL+")");

        Picasso.get()
                .load(imageURL)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        hideImagePlaceholder();
                        showImage();
                    }

                    @Override
                    public void onError(Exception e) {
                        showMessage(R.string.error_loading_image, Constants.ERROR_MSG);
                    }
                });
    }

    @Override
    public void setDescription(String description) {
        descriptionView.setText(description);
    }


    @Override
    public void showMessage(int msgId, String msgType) {
        int colorId;
        switch (msgType) {
            case Constants.INFO_MSG:
                colorId = R.color.info;
                break;
            case Constants.ERROR_MSG:
                colorId = R.color.error;
                break;
            default:
                colorId = R.color.undefined;
                break;
        }
        messageView.setTextColor(getResources().getColor(colorId));

        String msg = getResources().getString(msgId);
        messageView.setText(msg);

        MyUtils.show(messageView);
    }

    @Override
    public void hideMessage() {
        MyUtils.hide(messageView);
    }
}
