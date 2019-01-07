package ru.aakumykov.me.mvp.cards_grid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardsGrid_View extends BaseView implements
        iCardsGrid.View,
        CardsGrid_Adapter.iOnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener
{
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swiperefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.floatingActionButton) FloatingActionButton floatingActionButton;

    private iCardsGrid.Presenter presenter;
    private List<Card> cardsList = new ArrayList<>();
    private CardsGrid_Adapter dataAdapter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_grid_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        swiperefreshLayout.setOnRefreshListener(this);
        swiperefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        presenter = new CardsGrid_Presenter();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL);
//        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        dataAdapter = new CardsGrid_Adapter(this, cardsList);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(dataAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkView(this);
        dataAdapter.bindClickListener(this);

        if (firstRun) {
            loadList(true);
            firstRun = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
        dataAdapter.unbindClickListener();
    }

    @Override
    public void onUserLogin() {
        MyUtils.show(floatingActionButton);
    }

    @Override
    public void onUserLogout() {
        MyUtils.hide(floatingActionButton);
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

    @Override
    public void onRefresh() {
        swiperefreshLayout.setRefreshing(true);
        loadList(false);
    }


    // Интерфейсные методы
    @Override
    public void displayList(List<Card> list) {
        hideProgressBar();
        hideMsg();
        swiperefreshLayout.setRefreshing(false);

        this.cardsList.addAll(list);
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Card card = cardsList.get(position);
        if (null != card) {
            showCard(card.getKey());
        } else {
            showErrorMsg(R.string.CARDS_GRID_error_no_such_card);
        }
    }


    // События
    @OnClick(R.id.floatingActionButton)
    void fabClicked() {
        goCreateCard();
    }


    // Внутренние методы
    private void loadList(boolean showMessage) {
        if (showMessage) {
            MyUtils.show(progressBar);
            showInfoMsg(R.string.CARDS_GRID_loading_cards);
        }
        presenter.loadCards();
    }

    private void goListView() {
        Intent intent = new Intent(this, CardsList_View.class);
        startActivity(intent);
    }

    private void showCard(String cardKey) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardKey);
        startActivity(intent);
    }
}
