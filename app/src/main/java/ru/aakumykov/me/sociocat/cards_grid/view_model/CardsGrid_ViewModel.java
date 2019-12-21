package ru.aakumykov.me.sociocat.cards_grid.view_model;

import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;

public class CardsGrid_ViewModel extends ViewModel {

    private iCardsGrid.iPresenter presenter;
    private iCardsGrid.iDataAdapter dataAdapter;

    public void storeDataAdapter(iCardsGrid.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    public void storePresenter(iCardsGrid.iPresenter presenter) {
        this.presenter = presenter;
    }

    public iCardsGrid.iPresenter getPresenter() {
        return presenter;
    }

    public iCardsGrid.iDataAdapter getDataAdapter() {
        return dataAdapter;
    }
}
