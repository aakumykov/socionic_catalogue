package ru.aakumykov.me.mvp.card_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;

public class CardView_View extends AppCompatActivity implements iCardView.View {

    private final static String TAG = "CardView_View";

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    iCardView.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_activity);
        ButterKnife.bind(this);

        presenter = new CardView_Presenter();
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
    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void setQuote(String quote) {
        quoteView.setText(quote);
    }

    @Override
    public void setDescription(String description) {
        descriptionView.setText(description);
    }


    @Override
    public void showInfo(int msgId) {
        messageView.setTextColor(getResources().getColor(R.color.info));
        String msg = getResources().getString(msgId);
        showMessage(msg);
    }

    @Override
    public void showError(int msgId) {
        messageView.setTextColor(getResources().getColor(R.color.error));
        String msg = getResources().getString(msgId);
        showMessage(msg);
    }

    private void showMessage(String msg) {
        messageView.setText(msg);
        MyUtils.show(messageView);
    }

    @Override
    public void hideMessage() {
        MyUtils.hide(messageView);
    }
}
