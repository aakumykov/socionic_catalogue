package ru.aakumykov.me.mvp.cards_list_av;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.models.Card;

public class CardsListAV_View extends BaseView implements
        iCardsListAV.View,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener
{
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "CardsListAV_View";
    private iCardsListAV.Presenter presenter;
    private List<Card> cardsList;
    private CardsListAVAdapter cardsListAdapter;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_av_activity);
        ButterKnife.bind(this);

        setPageTitle("CardsList_ActiveView");

        presenter = new CardsListAV_Presenter();

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAVAdapter(this, R.layout.cards_list_item, cardsList);
        listView.setAdapter(cardsListAdapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setLongClickable(true);
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkModel(getCardsService());
        presenter.linkAuth(getAuthService());

        showProgressBar();
        showInfoMsg(R.string.loading_cards_list);
        presenter.loadList();
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkModel();
        presenter.unlinkAuth();
    }


    // Меню панели
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cards_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.actionCreateTextCard:
//                onAddCardButton(Constants.TEXT_CARD);
                break;

            case R.id.actionCreateImageCard:
//                onAddCardButton(Constants.IMAGE_CARD);
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }


    // Нажатия списка
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(pos: "+position+", id: "+id+")");

        // TODO: где контролировать эти данные?
        Card card = cardsList.get(position);

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemLongClick(pos: "+position+", id: "+id+")");
        return false;
    }


    // Интерфейсные методы
    @Override
    public void displayList(final List<Card> list) {
        Log.d(TAG, "displayList()");

        hideProgressBar();
        hideMsg();

        cardsList.clear();
        cardsList.addAll(list);
        cardsListAdapter.notifyDataSetChanged();
    }
}
