package ru.aakumykov.me.mvp.tags.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;


// TODO: попробовать extends iBaseView

public class TagShow_View extends BaseView implements
        iTags.ShowView
{
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.cardsCounter) TextView cardsCounter;
    @BindView(R.id.cardsList) LinearLayout cardsList;

    private final static String TAG = "TagShow_View";
    private iTags.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_show_activity);
        ButterKnife.bind(this);

        presenter = new Tags_Presenter();

        processIntent();
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
    public void onServiceBounded() {

    }
    @Override
    public void onServiceUnbounded() {

    }


    // Главные методы
    @Override
    public void displayTag(Tag tag) {
        Log.d(TAG, "displayTag(), "+tag);
        hideProgressBar();
        nameView.setText(tag.getName());
        cardsCounter.setText( String.valueOf( tag.getCards().size() ) );
    }

    @Override
    public void displayCards(List<Card> cardsList) {
        Log.d(TAG, "displayCards()");


    }


    // Внутренние методы
    private void processIntent() {
        Intent intent = getIntent();
        String tagKey = intent.getStringExtra(Constants.TAG_KEY);
        presenter.onShowPageReady(tagKey);
    }
}
