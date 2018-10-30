package ru.aakumykov.me.mvp.cards_list_av;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardsListAV_View extends BaseView implements
        iCardsListAV.View
{
    private final static String TAG = "CardsListAV_View";
    private iCardsListAV.Presenter presenter;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_av_activity);
        ButterKnife.bind(this);

        setPageTitle("CardsList_ActiveView");

        presenter = new CardsListAV_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkModel(getCardsService());
        presenter.linkAuth(getAuthService());
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkModel();
        presenter.unlinkAuth();
    }


    // Основные методы
    @Override
    public void displayList(List<Card> cardsList) {

    }
}
