package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import ru.aakumykov.me.mvp.card_view.CardView_View;
import ru.aakumykov.me.mvp.models.Card;

public class CardsList_View extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final static String TAG = "CardsList_View";

    private CardsList_ViewModel viewModel;
    private LiveData<List<Card>> liveData;

    private List<Card> cardsList;
    private CardsListAdapter cardsListAdapter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "CardsList_View.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        messageView.setText(R.string.loading_cards_list);
        messageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);
        listView.setOnItemClickListener(this);

        viewModel = ViewModelProviders.of(this).get(CardsList_ViewModel.class);

        liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<List<Card>>() {
            @Override
            public void onChanged(@Nullable List<Card> cards) {
                Log.d(TAG, "onChanged()");
                progressBar.setVisibility(View.GONE);
                messageView.setVisibility(View.GONE);
                cardsList.clear();
                cardsList.addAll(cards);
                cardsListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        // TODO: не загружать список повторно при повороте
        loadList();
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

    void loadList() {
        Log.d(TAG, "loadList()");
        viewModel.loadList();
    }
}
