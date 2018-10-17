package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.card_view.CardView_View;
import ru.aakumykov.me.mvp.models.Card;

// TODO: Пункт "обновить" в меню панели.

public class CardsList_View extends AppCompatActivity implements
        iCardsList.View,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "CardsList_View";

    private CardsList_ViewModel viewModel;
    private LiveData<List<Card>> liveData;

    private List<Card> cardsList;
    private CardsListAdapter cardsListAdapter;

//    private ActionBar actionBar;

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG+"_L-CYCLE", "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

//        actionBar = getSupportActionBar();

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);

        viewModel = ViewModelProviders.of(this).get(CardsList_ViewModel.class);

        liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<List<Card>>() {
            @Override
            public void onChanged(@Nullable List<Card> cards) {
                Log.d(TAG+"_LiveData", "=ПОСТУПИЛИ ЖИВЫЕ ДАННЫЕ=, ("+cards.size()+") "+cards);

                hideLoadingMessage();
                swipeRefreshLayout.setRefreshing(false);

                cardsList.clear();
                cardsList.addAll(cards);
                cardsListAdapter.notifyDataSetChanged();
            }
        });

        loadList(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG+"_L-CYCLE", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG+"_L-CYCLE", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG+"_L-CYCLE", "onDestroy()");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String cardKey = cardsList.get(position).getKey();

        Log.d(TAG, "onItemClick(), cardKey: "+cardKey);

        Intent intent = new Intent();
        // TODO: сделать независимым от конкретного класса
        intent.setClass(this, CardView_View.class);
        intent.putExtra(Constants.CARD_KEY, cardKey);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        loadList(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_card_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionCreateTextCard:
                onAddCardButton(Constants.TEXT_CARD);
                break;

            case R.id.actionCreateImageCard:
                onAddCardButton(Constants.IMAGE_CARD);
                break;

            case android.R.id.home:
                this.finish();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onAddCardButton(String cardType) {
        Log.d(TAG, "onAddCardButton("+cardType+")");

        switch (cardType) {
            case Constants.TEXT_CARD:
                break;
            case Constants.IMAGE_CARD:
                break;
            default:
                Log.e(TAG, "Wrong card type: "+cardType);
                return;
        }

        Intent intent = new Intent();
        intent.setClass(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD_TYPE, cardType);
        startActivity(intent);
    }


    private void loadList(boolean manualRefresh) {
        Log.d(TAG, "loadList("+manualRefresh+")");

        if (!manualRefresh)
            showLoadingMessage();

        viewModel.loadList(manualRefresh);
    }

    private void showLoadingMessage() {
        messageView.setText(R.string.getting_cards_list);
        messageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingMessage() {
        progressBar.setVisibility(View.GONE);
        messageView.setVisibility(View.GONE);
    }
}
