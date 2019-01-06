package ru.aakumykov.me.mvp.cards_grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardsGrid_View extends BaseView implements iCardsGrid.View {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.cardsGridView) GridView cardsGridView;

    private iCardsGrid.Presenter presenter;
    private List<Card> list;
    private CardsGrid_Adapter gridAdapter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_grid_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        presenter = new CardsGrid_Presenter();
        gridAdapter = new CardsGrid_Adapter(this, R.layout.cards_grid_item, list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firstRun) {
            loadList();
            firstRun = false;
        }
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void displayList(List<Card> list) {

    }


    // Внутренние методы
    private void loadList() {
        MyUtils.show(progressBar);
        showInfoMsg(R.string.CARDS_GRID_loading_cards);
        presenter.loadCards();
    }
}
