package ru.aakumykov.me.sociocat.card_show2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardShow2 extends BaseView implements
        iCardShow2.iPageView
{
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private final static String TAG = "CardShow2";
    private boolean firstRun = true;
    private iCardShow2.iDataAdapter dataAdapter;
    private iCardShow2.iPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);

        dataAdapter = new DataAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
        presenter = new CardShow2_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.bindView(this);

        if (firstRun) {
            firstRun = false;
            processInputIntent();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }



    @Override
    public void displayCard(Card card) {
        dataAdapter.setCard(card);
        presenter.onCardLoaded(card);
    }

    @Override
    public void displayComments(List<Comment> commentsList) {
        dataAdapter.setComments(commentsList);
    }



    // Внутренние методы
    private void processInputIntent() {
        Intent intent = getIntent();

        try {
            Card card = intent.getParcelableExtra(Constants.CARD);
            displayCard(card);
        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }
}
