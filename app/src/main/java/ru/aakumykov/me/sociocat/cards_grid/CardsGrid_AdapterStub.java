package ru.aakumykov.me.sociocat.cards_grid;

import android.view.View;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;

public class CardsGrid_AdapterStub implements iCardsGrig.iGridView {

    @Override
    public void linkPresenter(iCardsGrig.iPresenter presenter) {

    }

    @Override
    public void unlinkPresenter() {

    }

    @Override
    public void setList(List<iGridItem> inputList) {

    }

    @Override
    public void appendList(List<iGridItem> inputList) {

    }

    @Override
    public void restoreList(List<iGridItem> inputList) {

    }

    @Override
    public iGridItem getItem(int position) {
        return null;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return 0;
    }

    @Override
    public void updateItem(int position, iGridItem newGridItem) {

    }

    @Override
    public void removeItem(iGridItem gridItem) {

    }

    @Override
    public void hideLoadMoreItem(int position) {

    }

    @Override
    public void showThrobber() {

    }

    @Override
    public void showThrobber(int position) {

    }

    @Override
    public void hideThrobber() {

    }

    @Override
    public void hideThrobber(int position) {

    }

    @Override
    public void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder) {

    }
}
