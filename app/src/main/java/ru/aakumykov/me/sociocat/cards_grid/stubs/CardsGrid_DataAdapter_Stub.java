package ru.aakumykov.me.sociocat.cards_grid.stubs;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsGrid_DataAdapter_Stub implements iCardsGrid.iDataAdapter {

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
    public void insertList(int position, List<iGridItem> list) {

    }

    @Override
    public void insertItem(int position, iGridItem gridItem) {

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
    public iGridItem getGridItem(@NonNull Card searchedCard) {
        return null;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return 0;
    }

    @Override
    public GridItem_Card getLastCardItem() {
        return null;
    }

    @Override
    public GridItem_Card getFirstCardItem() {
        return null;
    }

    @Override
    public List<iGridItem> getList() {
        return null;
    }

    @Override
    public void showLoadMoreItem() {

    }

    @Override
    public void removeItem(iGridItem gridItem) {

    }

    @Override
    public void removeItem(int position) {

    }

    @Override
    public void updateItem(int position, Card card) {

    }

    @Override
    public void applyFilterToGrid(String filterKey) {

    }

    @Override
    public boolean hasData() {
        return false;
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
