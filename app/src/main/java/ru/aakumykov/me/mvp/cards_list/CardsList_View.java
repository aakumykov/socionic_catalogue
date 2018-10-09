package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardsList_View extends AppCompatActivity {

    private final static String TAG = "CardsList_View";

    private CardsList_ViewModel viewModel;
    private LiveData<List<Card>> liveData;

    private List<Card> cardsList;
    private CardsListAdapter cardsListAdapter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "CardsList_View.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);

        viewModel = ViewModelProviders.of(this).get(CardsList_ViewModel.class);
        liveData = viewModel.getLiveData();

        liveData.observe(this, new Observer<List<Card>>() {
            @Override
            public void onChanged(@Nullable List<Card> cards) {
                Log.d(TAG, "onChanged()");
                progressBar.setVisibility(View.GONE);
                cardsList.clear();
                cardsList.addAll(cards);
                cardsListAdapter.notifyDataSetChanged();
            }
        });

        // TODO: не загружать список повторно при повороте
        loadList();
    }

    void loadList() {
        Log.d(TAG, "loadList()");
        viewModel.loadList();
    }
}
