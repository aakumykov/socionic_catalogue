package ru.aakumykov.me.sociocat.card_show2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class CardShow2 extends BaseView implements
        iCardShow2.iPageView
{
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private iCardShow2.iDataAdapter dataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.card_show_activity);

        activateUpButton();
        setPageTitle(R.string.CARD_SHOW_page_title_short);

        dataAdapter = new DataAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // iCardShow2.iPageView
    @Override
    public void showCardThrobber() {

    }

    @Override
    public void hideCardThrobber() {

    }

    @Override
    public void showCommentsThrobber() {

    }

    @Override
    public void hideCommentsThrobber() {

    }

    @Override
    public void displayCard(Card card) {

    }

    @Override
    public void displayComments(String cardKey) {

    }

    @Override
    public void refreshCard(Card card) {

    }

    @Override
    public void refreshComments(String cardKey) {

    }
}
