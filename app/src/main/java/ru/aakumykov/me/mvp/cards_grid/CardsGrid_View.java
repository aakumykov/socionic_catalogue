package ru.aakumykov.me.mvp.cards_grid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardsGrid_View extends BaseView implements iCardsGrid.View {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
//    @BindView(R.id.cardsGridView) GridView gridView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private iCardsGrid.Presenter presenter;
    private List<Card> cardsList = new ArrayList<>();
//    private CardsGrid_ArrayAdapter dataAdapter;
    private CardsGrid_RecyclerAdapter dataAdapter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.cards_grid_activity);
        setContentView(R.layout.cards_grid_recycler_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        presenter = new CardsGrid_Presenter();

//        dataAdapter = new CardsGrid_ArrayAdapter(this, R.layout.cards_grid_item, cardsList);
        dataAdapter = new CardsGrid_RecyclerAdapter(this, cardsList);

//        gridView.setAdapter(dataAdapter);
        recyclerView.setAdapter(dataAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            loadList();
            firstRun = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.list_view, menu);
        menuInflater.inflate(R.menu.tags, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionListView:
                goListView();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }


    // Интерфейсные методы
    @Override
    public void displayList(List<Card> list) {
        hideProgressBar();
        hideMsg();

        this.cardsList.addAll(list);
        dataAdapter.notifyDataSetChanged();
    }


    // Внутренние методы
    private void loadList() {
        MyUtils.show(progressBar);
        showInfoMsg(R.string.CARDS_GRID_loading_cards);
        presenter.loadCards();
    }

    private void goListView() {
        Intent intent = new Intent(this, CardsList_View.class);
        startActivity(intent);
    }
}
