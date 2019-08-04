package ru.aakumykov.me.sociocat.cards_grid.view_stubs;

import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;

public class CardsGrid_AdapterStub implements iCardsGrid.iGridView {

    @Override
    public void linkPresenter(iCardsGrid.iPresenter presenter) {

    }

    @Override
    public void unlinkPresenter() {

    }

    @Override
    public void setList(List<iGridItem> inputList) {

    }

    @Override
    public void addList(List<iGridItem> inputList, int position, boolean forceLoadMoreItem, @Nullable Integer scrollToPosition) {

    }

    @Override
    public void restoreOriginalList() {

    }

    @Override
    public iGridItem getGridItem(int position) {
        return null;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return 0;
    }

    @Override
    public List<iGridItem> getList() {
        return null;
    }

    @Override
    public iGridItem getItemBeforeLoadmore(int loadmorePosition) {
        return null;
    }

    @Override
    public iGridItem getItemAfterLoadmore(int loadmorePosition) {
        return null;
    }

    @Override public void addItem(iGridItem gridItem) {

    }

    @Override
    public void updateItem(int position, iGridItem newGridItem) {

    }

    @Override
    public void removeItem(iGridItem gridItem) {

    }

    @Override
    public void applyFilterToGrid(String filterKey) {

    }

    @Override
    public void hideLoadMoreItem(int position) {

    }

    @Override
    public void showThrobber(int position) {

    }

    @Override
    public void hideThrobber(int position) {

    }

    @Override
    public void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder) {

    }

    @Override
    public void enableFiltering() {

    }

    @Override
    public void disableFiltering() {

    }

    @Override
    public boolean filterIsEnabled() {
        return false;
    }

}
